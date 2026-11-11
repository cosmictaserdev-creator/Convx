"""Read-only WebDAV/audio fixture. Python 3.10+, no packages or NAS required."""
import argparse
import base64
import math
import mimetypes
from pathlib import Path
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
import re
import struct
from urllib.parse import quote, unquote, urlsplit
import wave
import xml.etree.ElementTree as ET

DAV = "DAV:"
ET.register_namespace("d", DAV)


def create_audio(folder):
    folder.mkdir(parents=True, exist_ok=True)
    target = folder / "Convx - Seek test.wav"
    if target.exists():
        return
    rate = 8000
    with wave.open(str(target), "wb") as audio:
        audio.setparams((1, 2, rate, 0, "NONE", "not compressed"))
        # One minute with a different pitch every ten seconds makes seeks audible.
        for second in range(60):
            frequency = 220 + 110 * (second // 10)
            audio.writeframes(b"".join(struct.pack("<h", round(4000 * math.sin(2 * math.pi * frequency * t / rate)))
                                       for t in range(rate)))


def make_server(root, host="127.0.0.1", port=8766, username="convx", password="test"):
    root = Path(root).resolve()
    authorization = "Basic " + base64.b64encode(f"{username}:{password}".encode()).decode()

    class Handler(BaseHTTPRequestHandler):
        def target(self):
            if self.headers.get("Authorization") != authorization:
                self.send_response(401)
                self.send_header("WWW-Authenticate", 'Basic realm="Convx test"')
                self.send_header("Content-Length", "0")
                self.end_headers()
                return None
            url = urlsplit(self.path)
            path = unquote(url.path)
            prefix = next((p for p in ("/music/", "/redirect/") if path.startswith(p)), None)
            if prefix is None:
                self.send_error(404)
                return None
            target = (root / path[len(prefix):]).resolve()
            if not target.is_relative_to(root) or not target.exists():
                self.send_error(404)
                return None
            return target

        def do_PROPFIND(self):
            target = self.target()
            if target is None:
                return
            # Consume the request body even though the fixture returns a fixed property set.
            size = int(self.headers.get("Content-Length", "0"))
            if size > 8192:
                self.send_error(413)
                return
            self.rfile.read(size)
            if self.headers.get("Depth", "1") not in ("0", "1"):
                self.send_error(403)
                return
            result = ET.Element(f"{{{DAV}}}multistatus")
            entries = [target] + (sorted(target.iterdir()) if target.is_dir() and self.headers.get("Depth", "1") == "1" else [])
            base = "/redirect/" if self.path.startswith("/redirect/") else "/music/"
            for entry in entries:
                if not entry.resolve().is_relative_to(root):
                    continue
                response = ET.SubElement(result, f"{{{DAV}}}response")
                relative = entry.relative_to(root).as_posix()
                href = base + ("" if relative == "." else quote(relative)) + ("/" if entry.is_dir() and relative != "." else "")
                ET.SubElement(response, f"{{{DAV}}}href").text = href
                propstat = ET.SubElement(response, f"{{{DAV}}}propstat")
                prop = ET.SubElement(propstat, f"{{{DAV}}}prop")
                ET.SubElement(prop, f"{{{DAV}}}displayname").text = entry.name
                kind = ET.SubElement(prop, f"{{{DAV}}}resourcetype")
                if entry.is_dir():
                    ET.SubElement(kind, f"{{{DAV}}}collection")
                else:
                    ET.SubElement(prop, f"{{{DAV}}}getcontentlength").text = str(entry.stat().st_size)
                ET.SubElement(propstat, f"{{{DAV}}}status").text = "HTTP/1.1 200 OK"
            body = ET.tostring(result, encoding="utf-8", xml_declaration=True)
            self.send_response(207)
            self.send_header("Content-Type", "application/xml; charset=utf-8")
            self.send_header("Content-Length", str(len(body)))
            self.end_headers()
            self.wfile.write(body)

        def do_HEAD(self):
            self.send_audio(head=True)

        def do_GET(self):
            self.send_audio()

        def send_audio(self, head=False):
            target = self.target()
            if target is None:
                return
            if not target.is_file():
                self.send_error(404)
                return
            if self.path.startswith("/redirect/"):
                self.send_response(302)
                self.send_header("Location", "/music/" + quote(target.relative_to(root).as_posix()))
                self.send_header("Content-Length", "0")
                self.end_headers()
                return
            size = target.stat().st_size
            start, end = 0, size - 1
            requested = self.headers.get("Range")
            if requested:
                match = re.fullmatch(r"bytes=(\d+)-(\d*)", requested)
                if match:
                    start = int(match[1])
                    end = min(int(match[2]), end) if match[2] else end
                if not match or start >= size or start > end:
                    self.send_response(416)
                    self.send_header("Content-Range", f"bytes */{size}")
                    self.send_header("Content-Length", "0")
                    self.end_headers()
                    return
            self.send_response(206 if requested else 200)
            self.send_header("Content-Type", mimetypes.guess_type(target.name)[0] or "application/octet-stream")
            self.send_header("Accept-Ranges", "bytes")
            self.send_header("Content-Length", str(end - start + 1))
            if requested:
                self.send_header("Content-Range", f"bytes {start}-{end}/{size}")
            self.end_headers()
            if not head:
                with target.open("rb") as audio:
                    audio.seek(start)
                    remaining = end - start + 1
                    while remaining:
                        data = audio.read(min(65536, remaining))
                        if not data:
                            break
                        self.wfile.write(data)
                        remaining -= len(data)

        def log_message(self, fmt, *args):
            # Log method/status, never authorization headers or request URLs.
            print(f"{self.command}: {args[1] if len(args) > 1 else 'request'}", flush=True)

    return ThreadingHTTPServer((host, port), Handler)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--host", default="127.0.0.1", help="Use 0.0.0.0 to test from a phone on the same LAN")
    parser.add_argument("--port", type=int, default=8766)
    parser.add_argument("--folder", type=Path, help="Serve your own test music folder instead of generating a tone")
    args = parser.parse_args()
    folder = args.folder or Path("artifacts/network-storage-fixtures")
    if args.folder is None:
        create_audio(folder)
    if not folder.is_dir():
        parser.error("--folder must be an existing directory")
    print(f"WebDAV test server: http://{args.host}:{args.port}/music/", flush=True)
    print("Username: convx | Password: test | Redirect test base: /redirect/", flush=True)
    with make_server(folder, args.host, args.port) as server:
        try:
            server.serve_forever()
        except KeyboardInterrupt:
            pass
