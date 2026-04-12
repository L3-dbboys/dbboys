# one-off: fix garbled return in OracleDialect.getExportNoticeDefaultText
import re
path = r"src/com/dbboys/impl/dialect/oracle/OracleDialect.java"
with open(path, encoding="utf-8") as f:
    s = f.read()
pattern = r'(public String getExportNoticeDefaultText\(\) \{\s*return )"[^"]+";(\s*\})'
repl = r'\1"\u6a21\u5f0f\u5df2\u5bfc\u51fa\u5230\uff1a%s";\2'
s2, n = re.subn(pattern, repl, s, count=1)
if n != 1:
    raise SystemExit(f"expected 1 replacement, got {n}")
with open(path, "w", encoding="utf-8") as f:
    f.write(s2)
print("ok")
