package it.pezzotta.coinflow

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
fun sdkEqlOrAbv33() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU