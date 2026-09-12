<div align="center">
  <h1>NexaMusic</h1>
  <h3>NexaMusic is an open-source, Liquid Glass music player for Android</h3>

  <p>
    <a href="https://discord.gg/mCHqJXTxq">
      <img src="https://img.shields.io/badge/Discord-Join%20Us-5865F2?style=for-the-badge&logo=discord&logoColor=white" alt="Join Discord">
    </a>
  </p>
</div>

<hr>

<h2>🎵 About NexaMusic</h2>

<p><b>NexaMusic</b> is a free, open-source music player for Android that streams from YouTube Music, built with <b>Jetpack Compose</b> on a <b>Media3</b> ExoPlayer core. The UI is a custom <b>Liquid Glass</b> design system — frosted, refractive surfaces, iOS-style bouncy scrolling, and progressive blur chrome — instead of stock Material widgets.</p>

<p>NexaMusic is a fork of <a href="https://github.com/cosmictaserdev-creator/Convx">Convx</a>, which itself started as a fork of <a href="https://github.com/vivizzz007/vivi-music">vivi-music</a>; see <a href="#-credits">Credits</a> below.</p>

<hr>

<h2>✨ Features</h2>

<table align="center" width="100%">
  <tr valign="top">
    <td width="50%">
      <h3>🧊 Liquid Glass UI</h3>
      <ul>
        <li><b>Real backdrop blur:</b> frosted glass chrome (nav bar, floating buttons, sheets) that actually samples and refracts the content behind it, not a flat translucent color.</li>
        <li><b>iOS-style motion:</b> bouncy rubber-band overscroll, blurred page transitions, springy nav puck.</li>
        <li><b>Material You:</b> adaptive colors pulled from the currently playing artwork.</li>
      </ul>
    </td>
    <td width="50%">
      <h3>🎵 Streaming</h3>
      <ul>
        <li><b>Full YT Music catalog:</b> ad-free streaming and background playback with full notification/lock-screen controls.</li>
        <li><b>Offline downloads:</b> cache tracks locally with smart storage management.</li>
        <li><b>Lossless/high-quality audio</b> and a built-in equalizer.</li>
      </ul>
    </td>
  </tr>
  <tr valign="top">
    <td width="50%">
      <h3>📝 Lyrics & Social</h3>
      <ul>
        <li><b>Synced, karaoke-style lyrics</b> with word-by-word highlighting.</li>
        <li><b>Discord Rich Presence:</b> show what you're listening to on your profile.</li>
        <li><b>Listen Together:</b> sync playback with friends in real time.</li>
      </ul>
    </td>
    <td width="50%">
      <h3>🛡️ Privacy & Updates</h3>
      <ul>
        <li><b>Zero telemetry:</b> no trackers, no analytics, fully local library and preferences.</li>
        <li><b>Built-in updater:</b> in-app update checks and changelogs, no third-party store required.</li>
      </ul>
    </td>
  </tr>
</table>

<hr>

<h2>🏗️ Architecture</h2>

<ul>
  <li><b>UI:</b> Jetpack Compose, MVVM (<code>ui/screens</code> + <code>viewmodels</code>), navigated via <code>ui/screens/NavigationBuilder.kt</code>.</li>
  <li><b>Liquid Glass:</b> <code>ui/component/GlassEffect.kt</code> exposes <code>Modifier.liquidGlass(...)</code>, built on a vendored, source-included copy of <a href="https://github.com/Kyant0/backdrop">Kyant0/backdrop</a> under <code>ui/component/backdrop/</code>.</li>
  <li><b>Playback:</b> Media3 <code>ExoPlayer</code> service in <code>playback/MusicService.kt</code>.</li>
  <li><b>Data:</b> Room database (<code>db/</code>) for the local library, DataStore for preferences.</li>
  <li><b>YouTube Music access:</b> the <code>innertube</code> module — an unofficial InnerTube API client, kept separate from the app module.</li>
</ul>

<hr>

<h2>🤝 Contributing</h2>

<p>Contributions are welcome — bug reports, feature requests, and code.</p>

<ol>
  <li>Fork the repository</li>
  <li>Create your feature branch (<code>git checkout -b feature/AmazingFeature</code>)</li>
  <li>Commit your changes (<code>git commit -m 'Add some AmazingFeature'</code>)</li>
  <li>Push to the branch (<code>git push origin feature/AmazingFeature</code>)</li>
  <li>Open a Pull Request</li>
</ol>

<hr>

<h2>🛡️ Privacy & Data Collection</h2>

<ul>
  <li><strong>Zero Data Collection:</strong> we do <strong>not</strong> collect, store, or share any of your personal information, usage habits, or listening history.</li>
  <li><strong>100% Local:</strong> all your settings, downloaded tracks, and offline caches are stored securely on your device.</li>
  <li><strong>No Tracking:</strong> no hidden trackers, analytics, or background services monitoring your activity.</li>
</ul>

<hr>

<h2>📜 Disclaimer</h2>

<p>This project is <strong>not affiliated with, funded, authorized, endorsed by, or in any way associated with</strong> YouTube, Google LLC, or any of their affiliates and subsidiaries.</p>

<p><strong>NexaMusic</strong> is an independent project created for educational and personal use purposes.</p>

<hr>

<h2>📄 License</h2>
<p>This project is licensed under GPL-3.0 — see the <a href="LICENSE">LICENSE</a> file.</p>

<hr>

<div align="center">
  <h2>🙏 Credits</h2>
  <p>NexaMusic would not exist without the open-source projects it is built on.</p>

  <table border="0" cellpadding="10" cellspacing="0" width="90%">
    <tr valign="top">
      <td width="50%" align="left">
        <b>Project Lineage</b>
        <ul>
          <li><a href="https://github.com/vfsfitvnm/ViMusic"><b>ViMusic</b></a> — by vfsfitvnm. The original project this entire family is descended from.</li>
          <li><a href="https://github.com/vivizzz007/vivi-music"><b>vivi-music</b></a> — by Vividh P Ashokan. A fork of ViMusic that introduced the Material 3 Expressive UI.</li>
          <li><a href="https://github.com/cosmictaserdev-creator/Convx"><b>Convx</b></a> — by Aryan (CosmicTaser). A fork of vivi-music that introduced the Liquid Glass design system. NexaMusic is forked directly from Convx.</li>
        </ul>
      </td>
      <td width="50%" align="left">
        <b>Libraries & Features</b>
        <ul>
          <li><a href="https://github.com/Kyant0/backdrop"><b>Kyant0/backdrop</b></a> — the real-time backdrop blur/refraction library the Liquid Glass UI is built on.</li>
          <li><a href="https://github.com/better-lyrics/better-lyrics"><b>Better Lyrics</b></a> and <a href="https://github.com/maxrave-dev/SimpMusic"><b>SimpMusic</b></a> — synced lyrics sources.</li>
          <li><a href="https://github.com/ibratabian17/YouLyPlus"><b>YouLyPlus</b></a> — in-app lyrics styling.</li>
          <li><a href="https://github.com/monochrome-music/monochrome"><b>Monochrome</b></a> — the animated visualizer canvas.</li>
        </ul>
      </td>
    </tr>
  </table>

  <p>All third-party components remain the property of their respective authors and are used in accordance with their licenses.</p>
  <p align="center"><strong>Made with ❤️ for music lovers everywhere</strong></p>
</div>
