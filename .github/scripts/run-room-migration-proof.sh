#!/usr/bin/env bash
set -euo pipefail

app_apk="$(find app/build/outputs -type f -name '*.apk' ! -path '*/androidTest/*' -print -quit)"
test_apk="$(find app/build/outputs -type f -name '*.apk' -path '*/androidTest/*' -print -quit)"
test -n "$app_apk"
test -n "$test_apk"
printf 'App APK: %s\nTest APK: %s\n' "$app_apk" "$test_apk"

adb install -r "$app_apk"
adb install -r "$test_apk"

instrumentation_list="$(adb shell pm list instrumentation 2>&1 | tr -d '\r')"
printf 'Registered instrumentations:\n%s\n' "$instrumentation_list"
instrumentation_components="$(printf '%s\n' "$instrumentation_list" | sed -n 's/^instrumentation:\([^[:space:]]*\).*/\1/p')"
junit_runner="$(printf '%s\n' "$instrumentation_components" | grep -F 'RoomMigrationTestRunner' | head -n 1 || true)"
adb logcat -c

if [[ -z "$junit_runner" ]]; then
    echo "RoomMigrationTestRunner was not registered; refusing to run the proof with a different application startup path." >&2
    exit 1
fi

run_instrumentation() {
    local test_class="$1"
    shift
    local output

    printf 'Running %s with arguments: %s\n' "$test_class" "$*"
    adb logcat -c
    if output="$(adb shell am instrument -w "$@" -e class "$test_class" "$junit_runner" 2>&1)"; then
        instrumentation_status=0
    else
        instrumentation_status=$?
    fi
    printf '%s\n' "$output"
    if (( instrumentation_status != 0 )) || ! grep -Eq 'OK \([0-9]+ tests?\)' <<< "$output"; then
        echo "Recent Android logcat output:"
        adb logcat -d -v time | tail -n 300
        echo "Instrumentation failed: $test_class" >&2
        exit 1
    fi
}

# Each source schema runs in a fresh instrumentation process. This keeps Room
# and SQLite migration allocations isolated and makes a failing schema clear.
for ((start_version = 1; start_version < 40; start_version++)); do
    end_version=$((start_version + 1))
    run_instrumentation \
        com.convx.music.db.RoomSchemaMigrationMatrixTest \
        -e schemaStartVersion "$start_version" \
        -e schemaEndVersion "$end_version"
done

run_instrumentation com.convx.music.db.RoomSchemaMigrationTest
run_instrumentation com.convx.music.db.BackupArchiveStagedSettingsValidatorTest
