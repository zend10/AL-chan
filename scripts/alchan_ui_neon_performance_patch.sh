#!/data/data/com.termux/files/usr/bin/bash
set -Eeuo pipefail

REPO="${HOME}/AL-chan"
EXPECTED_BRANCH="tokyo/ui-home-cleanup-20261012"
EXPECTED_HEAD="8ea60113bcfde75a76d291fe13e4183ad3494f95"
STAMP="$(date +%Y%m%d-%H%M%S)"
BACKUP_ROOT="${HOME}/AL-chan-tokyo-backups/ui-neon-performance-${STAMP}"
REPORT_ROOT="${HOME}/AL-chan-audit-reports"
REPORT="${REPORT_ROOT}/ui-neon-performance-${STAMP}.txt"

fail() { printf '\n[ERROR] %s\n' "$*" >&2; exit 1; }
pass() { printf '[PASS] %s\n' "$*"; }

[ -d "$REPO/.git" ] || fail "Repository not found at $REPO"
command -v git >/dev/null 2>&1 || fail "git is not installed"
command -v python >/dev/null 2>&1 || fail "python is not installed in Termux"

BRANCH="$(git -C "$REPO" branch --show-current)"
HEAD="$(git -C "$REPO" rev-parse HEAD)"
printf 'Tokyo AL-chan UI/neon/performance patch\nRepository: %s\nExpected branch: %s\nActual branch: %s\nHEAD: %s\n' "$REPO" "$EXPECTED_BRANCH" "$BRANCH" "$HEAD"
[ "$BRANCH" = "$EXPECTED_BRANCH" ] || fail "Unexpected branch; no edits made"
[ "$HEAD" = "$EXPECTED_HEAD" ] || fail "HEAD differs from reviewed/pushed commit; no edits made"
[ -z "$(git -C "$REPO" status --porcelain)" ] || fail "Working tree/index are not clean. Inspect 'git status --short' first; no edits made"
pass "Expected branch, reviewed commit and clean tree"

FILES=(
  app/src/main/java/com/zen/alchan/ui/home/HomeViewModel.kt
  app/src/main/java/com/zen/alchan/ui/home/HomeRvAdapter.kt
  app/src/main/res/layout/layout_home_menu.xml
  app/src/main/res/values/colors.xml
  app/src/main/res/values/dimens.xml
  app/src/main/res/values/styles.xml
)
for f in "${FILES[@]}"; do [ -f "$REPO/$f" ] || fail "Missing expected file: $f"; done

mkdir -p "$BACKUP_ROOT" "$REPORT_ROOT"
for f in "${FILES[@]}"; do
  mkdir -p "$BACKUP_ROOT/$(dirname "$f")"
  cp -p "$REPO/$f" "$BACKUP_ROOT/$f"
done
pass "Backed up ${#FILES[@]} target files outside the repository: $BACKUP_ROOT"

export ALCHAN_REPO="$REPO"
python - <<'PY'
import os
from pathlib import Path

root = Path(os.environ["ALCHAN_REPO"])
files = {
    "vm": root / "app/src/main/java/com/zen/alchan/ui/home/HomeViewModel.kt",
    "adapter": root / "app/src/main/java/com/zen/alchan/ui/home/HomeRvAdapter.kt",
    "menu": root / "app/src/main/res/layout/layout_home_menu.xml",
    "colors": root / "app/src/main/res/values/colors.xml",
    "dimens": root / "app/src/main/res/values/dimens.xml",
    "styles": root / "app/src/main/res/values/styles.xml",
}
contents = {k: p.read_text(encoding="utf-8") for k,p in files.items()}

def replace_once(key, old, new, label, expected=1):
    c = contents[key]
    n = c.count(old)
    if n != expected:
        raise SystemExit(f"[STOP] {label}: expected {expected} exact match(es), found {n}. No source files have been written.")
    contents[key] = c.replace(old, new)
    print(f"[PASS] Prepared: {label}")

# 1) Put Continue Watching before Releasing Today in settings-driven section order.
replace_once(
    "vm",
    "        if (homeAppSetting.showHomeReleasingToday != false) nextItems.add(releasing)\n        if (homeAppSetting.showHomeContinueWatching != false) nextItems.add(continuing)",
    "        if (homeAppSetting.showHomeContinueWatching != false) nextItems.add(continuing)\n        if (homeAppSetting.showHomeReleasingToday != false) nextItems.add(releasing)",
    "settings-driven section order",
)

# 2) Put Continue Watching before Releasing Today in initial home list construction.
replace_once(
    "vm",
    "            if (homeAppSetting.showHomeReleasingToday != false) {\n                initialItems.add(HomeItem(viewType = HomeItem.VIEW_TYPE_RELEASING_TODAY))\n            }\n            if (homeAppSetting.showHomeContinueWatching != false) {\n                initialItems.add(HomeItem(viewType = HomeItem.VIEW_TYPE_CONTINUE_WATCHING))\n            }",
    "            if (homeAppSetting.showHomeContinueWatching != false) {\n                initialItems.add(HomeItem(viewType = HomeItem.VIEW_TYPE_CONTINUE_WATCHING))\n            }\n            if (homeAppSetting.showHomeReleasingToday != false) {\n                initialItems.add(HomeItem(viewType = HomeItem.VIEW_TYPE_RELEASING_TODAY))\n            }",
    "initial home section order",
)

# 3) If the releasing-today result arrives asynchronously, insert it after Continue Watching.
replace_once(
    "vm",
    "                                        val menuIndex = currentHomeList.indexOfFirst { it.viewType == HomeItem.VIEW_TYPE_MENU }\n                                        if (menuIndex != -1 && homeAppSetting.showHomeReleasingToday != false) {\n                                            currentHomeList.add(menuIndex + 1, HomeItem(releasingToday = it, viewType = HomeItem.VIEW_TYPE_RELEASING_TODAY))\n                                        }",
    "                                        val menuIndex = currentHomeList.indexOfFirst { it.viewType == HomeItem.VIEW_TYPE_MENU }\n                                        val continueWatchingIndex = currentHomeList.indexOfFirst { it.viewType == HomeItem.VIEW_TYPE_CONTINUE_WATCHING }\n                                        if (menuIndex != -1 && homeAppSetting.showHomeReleasingToday != false) {\n                                            val insertIndex = if (continueWatchingIndex != -1) continueWatchingIndex + 1 else menuIndex + 1\n                                            currentHomeList.add(insertIndex.coerceAtMost(currentHomeList.size), HomeItem(releasingToday = it, viewType = HomeItem.VIEW_TYPE_RELEASING_TODAY))\n                                        }",
    "asynchronous Releasing Today insertion order",
)

# 4) If a Continue Watching placeholder is absent, insert it directly after the home menu.
replace_once(
    "vm",
    "                            val releasingIndex = currentHomeList.indexOfFirst { it.viewType == HomeItem.VIEW_TYPE_RELEASING_TODAY }\n                            val insertIndex = if (releasingIndex != -1) releasingIndex + 1 else menuIndex + 1\n                            currentHomeList.add(insertIndex.coerceAtMost(currentHomeList.size), continueWatchingItem)",
    "                            val insertIndex = menuIndex + 1\n                            currentHomeList.add(insertIndex.coerceAtMost(currentHomeList.size), continueWatchingItem)",
    "Continue Watching fallback insertion order",
)

# 5) Keep the home preview leaner: six recent entries instead of twelve.
replace_once("vm", ".take(12)", ".take(6)", "Continue Watching preview cap (initial list and local updates)", expected=2)

# 6) Reuse nested adapters instead of constructing them on every parent bind.
replace_once(
    "adapter",
    "    inner class ReleasingTodayViewHolder(private val binding: LayoutHomeReleasingTodayBinding) : ViewHolder(binding) {\n        override fun bind(item: HomeItem, index: Int) {\n            with(binding) {\n                if (item.releasingToday.isNotEmpty()) {\n                    releasingTodayRecyclerView.adapter = ReleasingTodayRvAdapter(context, item.releasingToday, appSetting, listener.releasingTodayListener)\n                    releasingTodayRecyclerView.show(true)\n                    releasingTodayEmptyText.show(false)",
    "    inner class ReleasingTodayViewHolder(private val binding: LayoutHomeReleasingTodayBinding) : ViewHolder(binding) {\n        private var releasingAdapter: ReleasingTodayRvAdapter? = null\n\n        override fun bind(item: HomeItem, index: Int) {\n            with(binding) {\n                if (item.releasingToday.isNotEmpty()) {\n                    val existingAdapter = releasingAdapter\n                    if (existingAdapter == null) {\n                        ReleasingTodayRvAdapter(context, item.releasingToday, appSetting, listener.releasingTodayListener).also {\n                            releasingAdapter = it\n                            releasingTodayRecyclerView.adapter = it\n                        }\n                    } else {\n                        existingAdapter.updateData(item.releasingToday)\n                    }\n                    releasingTodayRecyclerView.show(true)\n                    releasingTodayEmptyText.show(false)",
    "reuse Releasing Today nested adapter",
)
replace_once(
    "adapter",
    "    inner class ContinueWatchingViewHolder(private val binding: LayoutHomeContinueWatchingBinding) : ViewHolder(binding) {\n        override fun bind(item: HomeItem, index: Int) {\n            with(binding) {\n                if (item.continueWatching.isNotEmpty()) {\n                    continueWatchingRecyclerView.adapter = ContinueWatchingRvAdapter(context, item.continueWatching, appSetting, listener.releasingTodayListener)\n                    continueWatchingRecyclerView.show(true)\n                    continueWatchingEmptyText.show(false)",
    "    inner class ContinueWatchingViewHolder(private val binding: LayoutHomeContinueWatchingBinding) : ViewHolder(binding) {\n        private var continueWatchingAdapter: ContinueWatchingRvAdapter? = null\n\n        override fun bind(item: HomeItem, index: Int) {\n            with(binding) {\n                if (item.continueWatching.isNotEmpty()) {\n                    val existingAdapter = continueWatchingAdapter\n                    if (existingAdapter == null) {\n                        ContinueWatchingRvAdapter(context, item.continueWatching, appSetting, listener.releasingTodayListener).also {\n                            continueWatchingAdapter = it\n                            continueWatchingRecyclerView.adapter = it\n                        }\n                    } else {\n                        existingAdapter.updateData(item.continueWatching)\n                    }\n                    continueWatchingRecyclerView.show(true)\n                    continueWatchingEmptyText.show(false)",
    "reuse Continue Watching nested adapter",
)
replace_once(
    "adapter",
    "    inner class TrendingMediaViewHolder(private val binding: LayoutHomeTrendingBinding) : ViewHolder(binding) {\n        override fun bind(item: HomeItem, index: Int) {",
    "    inner class TrendingMediaViewHolder(private val binding: LayoutHomeTrendingBinding) : ViewHolder(binding) {\n        private var trendingAdapter: TrendingMediaRvAdapter? = null\n\n        override fun bind(item: HomeItem, index: Int) {",
    "add retained Trending adapter to holder",
)
replace_once(
    "adapter",
    "                if (item.media.isNotEmpty()) {\n                    trendingListRecyclerView.adapter = TrendingMediaRvAdapter(context, item.media, appSetting, width, listener.trendingMediaListener)\n                    trendingProgressBar.show(false)",
    "                if (item.media.isNotEmpty()) {\n                    val existingAdapter = trendingAdapter\n                    if (existingAdapter == null) {\n                        TrendingMediaRvAdapter(context, item.media, appSetting, width, listener.trendingMediaListener).also {\n                            trendingAdapter = it\n                            trendingListRecyclerView.adapter = it\n                        }\n                    } else {\n                        existingAdapter.updateData(item.media)\n                    }\n                    trendingProgressBar.show(false)",
    "reuse Trending nested adapter",
)

# 7) Smaller circular shortcuts; size is intentionally fixed so removing Reviews does not enlarge the remaining three.
menu_pattern = '''                app:layout_constraintTop_toTopOf="parent"\n                app:layout_constraintDimensionRatio="1:1"\n                android:layout_width="match_parent"\n                android:layout_height="0dp">'''
menu_replacement = '''                app:layout_constraintTop_toTopOf="parent"\n                app:layout_constraintStart_toStartOf="parent"\n                app:layout_constraintEnd_toEndOf="parent"\n                app:layout_constraintDimensionRatio="1:1"\n                android:layout_width="@dimen/homeMenuIconDiameter"\n                android:layout_height="0dp">'''
replace_once("menu", menu_pattern, menu_replacement, "fixed compact shortcut diameter", expected=3)
replace_once("menu", 'android:padding="@dimen/marginNormal"', 'android:padding="@dimen/marginSmall"', "smaller inner shortcut icon padding", expected=3)

# 8) Add the sizing dimension.
replace_once(
    "dimens",
    '    <dimen name="iconSmallSize">18dp</dimen>\n',
    '    <dimen name="iconSmallSize">18dp</dimen>\n    <dimen name="homeMenuIconDiameter">72dp</dimen>\n',
    "home shortcut size dimension",
)

# 9) Tone down the primary red and add a restrained neon-pink accent for Android accent controls only.
replace_once(
    "colors",
    '    <color name="tokyoRed">#E13333</color>',
    '    <color name="tokyoRed">#D43A53</color>\n    <color name="tokyoRedTransparent50">#80D43A53</color>\n    <color name="tokyoNeon">#FF3974</color>\n    <color name="tokyoNeonTransparent50">#80FF3974</color>',
    "muted Tokyo crimson and restrained neon palette",
)
replace_once(
    "styles",
    '''    <style name="AppTheme.ThemeTokyoRed" parent="DarkAppTheme">\n        <item name="themePrimaryColor">@color/tokyoRed</item>\n        <item name="themeSecondaryColor">@color/white</item>\n        <item name="themeSecondaryTransparent50Color">@color/whiteTransparent50</item>\n        <item name="themeSecondaryTransparent20Color">@color/whiteTransparent36</item>\n        <item name="themeNegativeColor">@color/anilistBrightRed</item>\n        <item name="themeNegativeTransparent50Color">@color/anilistBrightRedTransparent50</item>\n    </style>''',
    '''    <style name="AppTheme.ThemeTokyoRed" parent="DarkAppTheme">\n        <item name="themePrimaryColor">@color/tokyoRed</item>\n        <item name="themeSecondaryColor">@color/white</item>\n        <item name="themeSecondaryTransparent50Color">@color/whiteTransparent50</item>\n        <item name="themeSecondaryTransparent20Color">@color/whiteTransparent36</item>\n        <item name="themeNegativeColor">@color/tokyoRed</item>\n        <item name="themeNegativeTransparent50Color">@color/tokyoRedTransparent50</item>\n        <item name="colorAccent">@color/tokyoNeon</item>\n    </style>''',
    "Tokyo Theme subdued primary with neon accent controls",
)

# Extra structural assertions before any write.
assert contents["vm"].count(".take(6)") >= 2, "Expected Continue Watching preview caps missing"
assert "val insertIndex = if (continueWatchingIndex != -1) continueWatchingIndex + 1 else menuIndex + 1" in contents["vm"]
assert contents["menu"].count('@dimen/homeMenuIconDiameter') == 3
assert "@color/tokyoNeon" in contents["styles"]

# Write all six files only after every exact replacement matched.
for k, p in files.items():
    p.write_text(contents[k], encoding="utf-8")
print("[PASS] All exact source transformations applied; no partial edit was written during pattern checks")
PY

# Static validation of changed resources and intended code structure.
export ALCHAN_REPO="$REPO"
python - <<'PY' | tee "$REPORT"
import os
import xml.etree.ElementTree as ET
from pathlib import Path
root=Path(os.environ["ALCHAN_REPO"])
checks=[]
def check(name, ok):
    checks.append((name, bool(ok)))
    print(f"[{'PASS' if ok else 'FAIL'}] {name}")
vm=(root/'app/src/main/java/com/zen/alchan/ui/home/HomeViewModel.kt').read_text()
ad=(root/'app/src/main/java/com/zen/alchan/ui/home/HomeRvAdapter.kt').read_text()
menu=(root/'app/src/main/res/layout/layout_home_menu.xml').read_text()
colors=(root/'app/src/main/res/values/colors.xml').read_text()
dimens=(root/'app/src/main/res/values/dimens.xml').read_text()
styles=(root/'app/src/main/res/values/styles.xml').read_text()

# Section order both in initial construction and settings synchronization.
check('Continue Watching ordered before Releasing Today in settings sync',
      'if (homeAppSetting.showHomeContinueWatching != false) nextItems.add(continuing)\n        if (homeAppSetting.showHomeReleasingToday != false) nextItems.add(releasing)' in vm)
check('Initial home section order places Continue Watching before Releasing Today',
      vm.index('initialItems.add(HomeItem(viewType = HomeItem.VIEW_TYPE_CONTINUE_WATCHING))') < vm.index('initialItems.add(HomeItem(viewType = HomeItem.VIEW_TYPE_RELEASING_TODAY)')
      )
check('Asynchronous Releasing Today insertion follows Continue Watching',
      'val insertIndex = if (continueWatchingIndex != -1) continueWatchingIndex + 1 else menuIndex + 1' in vm)
check('Continue Watching fallback insertion is directly after home menu',
      'val insertIndex = menuIndex + 1\n                            currentHomeList.add(insertIndex.coerceAtMost(currentHomeList.size), continueWatchingItem)' in vm)
check('Home preview is limited to six most-recent anime entries', vm.count('.take(6)') >= 2 and '.take(12)' not in vm)
check('Releasing Today adapter is retained and updated', 'private var releasingAdapter:' in ad and 'existingAdapter.updateData(item.releasingToday)' in ad)
check('Continue Watching adapter is retained and updated', 'private var continueWatchingAdapter:' in ad and 'existingAdapter.updateData(item.continueWatching)' in ad)
check('Trending adapter is retained and updated', 'private var trendingAdapter:' in ad and 'existingAdapter.updateData(item.media)' in ad)
check('All three home shortcuts use a fixed compact diameter', menu.count('@dimen/homeMenuIconDiameter') == 3 and menu.count('android:layout_width="match_parent"') >= 4)
check('Inner shortcut icon padding reduced to marginSmall', menu.count('android:padding="@dimen/marginSmall"') == 3)
check('Compact icon dimension is 72dp', '<dimen name="homeMenuIconDiameter">72dp</dimen>' in dimens)
check('Tokyo crimson changed from the original bright red', '<color name="tokyoRed">#D43A53</color>' in colors)
check('Neon accent resource exists', '<color name="tokyoNeon">#FF3974</color>' in colors)
check('Tokyo theme applies neon accent without making all primary surfaces neon', '<item name="themePrimaryColor">@color/tokyoRed</item>' in styles and '<item name="colorAccent">@color/tokyoNeon</item>' in styles)

xml_paths = [
'app/src/main/res/layout/layout_home_menu.xml',
'app/src/main/res/values/colors.xml',
'app/src/main/res/values/dimens.xml',
'app/src/main/res/values/styles.xml',
]
for rel in xml_paths:
    try:
        ET.parse(root/rel)
        check(f'XML parses: {rel}', True)
    except Exception as e:
        check(f'XML parses: {rel}', False)
        print(f'  XML error: {e}')

failed=[n for n,ok in checks if not ok]
print(f'\nSummary: {len(checks)-len(failed)} passed, {len(failed)} failed')
print('Scope: UI ordering, compact icons, toned-down Tokyo theme, nested adapter reuse, six-card Continue Watching preview.')
print('Static validation only; no Gradle build, install, commit, or push was performed.')
if failed:
    print('Failures: ' + '; '.join(failed))
    raise SystemExit(1)
PY

if ! git -C "$REPO" diff --check; then
  printf '\n[ERROR] git diff --check failed. Backup preserved at %s\n' "$BACKUP_ROOT" >&2
  exit 1
fi
pass "Git whitespace check"
printf '\nChanged files:\n'
git -C "$REPO" status --short
printf '\nDiff summary:\n'
git -C "$REPO" diff --stat
printf '\nBackup: %s\nReport: %s\n' "$BACKUP_ROOT" "$REPORT"
printf '\n[Tokyo UI Patch] Complete. Changes are LOCAL and UNSTAGED. No commit, push, build, or install was performed.\n'
printf 'Next: review the diff, then run the read-only validator or send me the final output.\n'
