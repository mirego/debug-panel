package com.mirego.debugpanel.sample

import platform.posix.exit

actual fun killApp() {
    exit(0)
}
