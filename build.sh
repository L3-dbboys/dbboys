#!/bin/bash
set -e

# ========== DBboys Linux Build Script ==========
# Requires: JDK 25, JavaFX jmods 25, zip tool
# Equivalent to build.bat on Windows

# ---- Config ----
# JavaFX jmods path
#   override: JAVAFX_JMODS=/custom/path ./build.sh
#   default:  /opt/javafx-jmods-25.0.3
JAVAFX_JMODS="${JAVAFX_JMODS:-/opt/javafx-jmods-25.0.3}"
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "=== DBboys Linux Build ==="
echo "Project dir : $PROJECT_DIR"
echo "JavaFX jmods: $JAVAFX_JMODS"
echo ""

# ---- Step 1: Compile Java sources ----
echo "[1/7] Compiling Java sources..."
find "$PROJECT_DIR/src" -name "*.java" > "$PROJECT_DIR/sources.txt"
javac -encoding UTF-8 \
  -d "$PROJECT_DIR/bin" \
  -sourcepath "$PROJECT_DIR/src" \
  -cp "$PROJECT_DIR/lib/lib_modular/*:$PROJECT_DIR/lib/lib_nonmodular/*" \
  @"$PROJECT_DIR/sources.txt"
echo "  Source compilation completed."

# ---- Step 2: Copy runtime resources ----
echo "[2/7] Copying runtime resources..."
mkdir -p $PROJECT_DIR/bin/com/dbboys/ui/fxml
mkdir -p $PROJECT_DIR/bin/com/dbboys/ui/css
mkdir -p $PROJECT_DIR/bin/com/dbboys/infra/i18n
cp -r "$PROJECT_DIR/src/com/dbboys/ui/fxml/"*   "$PROJECT_DIR/bin/com/dbboys/ui/fxml/"   2>/dev/null || true
cp -r "$PROJECT_DIR/src/com/dbboys/ui/css/"*    "$PROJECT_DIR/bin/com/dbboys/ui/css/"    2>/dev/null || true
cp    "$PROJECT_DIR/src/com/dbboys/infra/i18n/"*.properties "$PROJECT_DIR/bin/com/dbboys/infra/i18n/" 2>/dev/null || true
cp    "$PROJECT_DIR/src/IKAnalyzer.cfg.xml"  "$PROJECT_DIR/bin/"                    2>/dev/null || true
cp    "$PROJECT_DIR/src/ik-stopwords.dic"     "$PROJECT_DIR/bin/"                    2>/dev/null || true
echo "  Resources copied."

# ---- Step 3: Build JAR ----
echo "[3/7] Creating dbboys.jar..."
jar --create \
  --file "$PROJECT_DIR/lib/lib_nonmodular/dbboys.jar" \
  --main-class com.dbboys.app.Main \
  -C "$PROJECT_DIR/bin" .
echo "  dbboys.jar created."

# ---- Step 4: Create minimized JRE ----
echo "[4/7] Creating minimized JRE (jlink)..."
jlink \
  --module-path "$JAVAFX_JMODS:$PROJECT_DIR/lib/lib_modular" \
  --add-modules javafx.fxml,org.json,net.sf.jsqlparser,javafx.swing,org.controlsfx.controls,org.commonmark,java.sql,java.naming,java.management,java.security.jgss,java.transaction.xa,java.xml,jdk.crypto.ec,jdk.security.auth,org.apache.lucene.queryparser,org.apache.lucene.sandbox,org.apache.lucene.core,org.apache.logging.log4j,org.apache.logging.log4j.core \
  --output "$PROJECT_DIR/jre-min" \
  --strip-debug \
  --no-man-pages \
  --no-header-files
echo "  Minimized JRE created."

# ---- Step 5: Package app-image ----
echo "[5/7] Packaging app-image (jpackage)..."
jpackage --type app-image \
  --name dbboys \
  --dest "$PROJECT_DIR" \
  --input "$PROJECT_DIR/lib/lib_nonmodular" \
  --main-jar dbboys.jar \
  --main-class com.dbboys.app.Main \
  --runtime-image "$PROJECT_DIR/jre-min" \
  --icon "$PROJECT_DIR/images/logo.png" \
  --java-options "-Xmx1024m" \
  --java-options "-Dlog4j2.configurationFile=etc/log4j2.xml"
echo "  Packaging finished."

# ---- Step 6: Cleanup temp files ----
echo "[6/7] Cleaning up..."
rm -f "$PROJECT_DIR/sources.txt"
rm -rf "$PROJECT_DIR/bin"
rm -f "$PROJECT_DIR/lib/lib_nonmodular/dbboys.jar"
rm -rf "$PROJECT_DIR/jre-min"
echo "  Temp files cleaned."

# ---- Step 7: Copy resources & zip ----
echo "[7/7] Assembling distribution..."
cp -r "$PROJECT_DIR/docs"   "$PROJECT_DIR/dbboys/docs/"
cp -r "$PROJECT_DIR/extlib" "$PROJECT_DIR/dbboys/extlib/"
cp -r "$PROJECT_DIR/images" "$PROJECT_DIR/dbboys/images/"
cp -r "$PROJECT_DIR/etc"    "$PROJECT_DIR/dbboys/etc/"

# 

cat > "$PROJECT_DIR/dbboys/start.sh" << 'STARTEOF'
#!/bin/bash
DIR="$(cd "$(dirname "$0")" && pwd)"
"$DIR/bin/dbboys"
STARTEOF
chmod +x "$PROJECT_DIR/dbboys/start.sh"

echo "  Folders copied + start.sh created."

rm -f "$PROJECT_DIR/dbboys.zip"
cd "$PROJECT_DIR"
zip -r -5 "$PROJECT_DIR/dbboys.zip" "dbboys/"
echo "  Packaged dbboys.zip."

rm -rf "$PROJECT_DIR/dbboys"
echo "  Deleted dbboys directory."

echo ""
echo "=== Build complete: $PROJECT_DIR/dbboys.zip ==="
echo ""
echo "Usage on target Linux machine:"
echo "  unzip dbboys.zip"
echo "  cd dbboys"
echo "  chmod +x start.sh && ./start.sh"
