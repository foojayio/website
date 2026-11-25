---
title: "How to test React Native OTA updates across two Android cold starts"
date: "2026-01-01"
description: "Use two Android cold starts to check which JavaScript bundle runs after a React Native OTA update and whether the SDK rolls it back."
authors: ["kirill-novikov"]
image: "cold-starts.png"
categories: ["Testing"]
---

An Android tester downloads a React Native update that fixes a blank delivery receipt. The progress bar reaches 100%, but the receipt on screen is still blank. With Revopush set to `ON_NEXT_RESTART`, that’s an expected result for this session. To verify the fix, cold-start the app, open the receipt, then cold-start it once more. The first launch exercises the new JavaScript. The second catches a bundle that ran once and then rolled back.

## Give the test a receipt and a release label

Use an Android release build pointed at Staging. The SDK calls below are from `@revopush/react-native-code-push` 2.6.0. Pick a saved order that reliably reproduces the blank receipt and record its order ID. Confirm that the receipt is blank on the old bundle. Keep that order on the device throughout the test. Clearing app data between launches would remove the downloaded update along with the evidence you need.

For this example, assume the old OTA label is `v11`, the only newer Staging release is `v12`, and the fix changes the JavaScript that renders the receipt total. A [Revopush differential update](https://revopush.org/revopush-2-0-diff-updates) can reuse bundle data already on the device. Download progress tells you how much of that transfer has finished. It can’t tell you which bundle drew the receipt.

In a test build, await `codePush.getUpdateMetadata(codePush.UpdateState.RUNNING)` at startup and log the returned package’s `label`. If the app is running the bundle embedded in the binary, the result can be `null`. Log that as “embedded” so a tester can distinguish it from a missing log entry. [Revopush JavaScript API](https://docs.revopush.org/sdk/api-js#codepush-getupdatemetadata)

| Checkpoint | Expected running label | What to inspect |
| --- | --- | --- |
| Download finished, app still open | v11 | The same order still shows the old receipt |
| First cold start | v12 | The receipt shows the expected total |
| Second cold start | v12 | The fix survives another launch |

These are expected results for the example. Use the actual release labels and expected receipt total from your own fixture.

## Restart the Android process

Wait for `sync()` to report `UPDATE_INSTALLED` before stopping the process. With `ON_NEXT_RESTART`, the SDK has saved the package at that point and the old JavaScript is still running.

An Android instrumentation test that calls `ActivityScenario.recreate()` destroys and recreates the activity. That exercises a different lifecycle from stopping the app process. On a test device, use `adb` from Android SDK Platform-Tools (36.0.0 in this example) with your Staging application ID, then tap the launcher icon. Repeat once after checking the receipt. [AndroidX ActivityScenario](https://developer.android.com/reference/androidx/test/core/app/ActivityScenario) · [Android Debug Bridge](https://developer.android.com/tools/adb)

```sh
adb shell am force-stop com.example.delivery.staging
```

If the receipt looks right on the first launch and the label reverts on the second, inspect startup. Revopush’s `sync()` acknowledges the running update when called at startup. Apps that install packages manually must call `notifyAppReady()` from the updated bundle after startup succeeds. Without that acknowledgement, the SDK can restore the previous bundle on the next restart. [Revopush startup acknowledgement](https://docs.revopush.org/sdk/api-js#codepush-notifyappready)

For the release ticket, record the running label and receipt total after each cold start. If both launches show `v12` but the total is still blank, the new bundle ran. Investigate what the receipt screen did with that order before changing the rollout settings.
