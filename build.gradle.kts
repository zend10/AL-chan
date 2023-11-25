// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    //trick: for the same plugin versions in all sub-modules
    id("com.android.application").version("8.1.0").apply(false)
    id("com.android.library").version("8.1.0").apply(false)
    kotlin("android").version("1.9.20").apply(false)
    kotlin("multiplatform").version("1.9.20").apply(false)
    id("com.apollographql.apollo3").version("3.8.1").apply(false)
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}