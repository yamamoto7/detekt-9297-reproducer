# Reproducer for detekt/detekt#9297

This repository only exists to reproduce [detekt/detekt#9297](https://github.com/detekt/detekt/issues/9297):
with full analysis, a ktlint wrapper rule that has `ignoreAnnotated` crashes the whole run with

```
`findFirCompiledSymbol` only works on compiled declarations, but the given declaration is not compiled.
```

The crash needs all three of these:

- a ktlint wrapper rule (here `ktlint>MaximumLineLength`)
- `ignoreAnnotated` on that rule
- full analysis mode (`detektMain`, not `detekt`)

## Versions

- detekt Gradle plugin and `detekt-rules-ktlint-wrapper` 2.0.0-alpha.6
- Kotlin 2.4.10, Gradle 9.9.0 (wrapper included)
- Tested with JDK 17 and JDK 25

## Steps

```
git clone https://github.com/yamamoto7/detekt-9297-reproducer
cd detekt-9297-reproducer
./gradlew detekt       # light mode: works
./gradlew detektMain   # full mode: crashes
```

The [Reproduce workflow](../../actions/workflows/reproduce.yml) runs the same two tasks. Its `detektMain` step fails while the bug exists.

## Expected

Both tasks report only `Screen.kt:6`. The function on line 4 has `@Preview`, so `ignoreAnnotated` suppresses its finding.

```
e: src/main/kotlin/sample/Screen.kt:6:40 Exceeded max line length (40) [MaximumLineLength]
```

`ignoreFailures = true` is set so that findings alone don't fail the build. Only the crash fails it.

## Actual

`./gradlew detekt` behaves as expected. `./gradlew detektMain` fails:

```
Execution failed for task ':detektMain' (registered by plugin 'dev.detekt').
> Analyzing .../src/main/kotlin/sample/Screen.kt led to an exception.
  Location: org.jetbrains.kotlin.analysis.low.level.api.fir.api.LLResolutionFacade.findCompiledFirSymbol(LLResolutionFacade.kt:280)
  The original exception message was: `findFirCompiledSymbol` only works on compiled declarations, but the given declaration is not compiled.
```

Removing `ignoreAnnotated` from `detekt.yml` makes `detektMain` work too (it then reports both lines 4 and 6).

## Files

| File | Purpose |
|---|---|
| `detekt.yml` | enables `ktlint>MaximumLineLength` with `maxLineLength: 40` and `ignoreAnnotated: ['Preview']` |
| `src/main/kotlin/sample/Preview.kt` | `annotation class Preview` |
| `src/main/kotlin/sample/Screen.kt` | one annotated and one plain function, both longer than 40 characters |
| `build.gradle.kts` | applies the detekt plugin and the ktlint wrapper |
