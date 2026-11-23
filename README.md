# CS 361 Scanner Project 

Scanner project for CS361 Class, created to be done in a group. 

## Members: 

Aziz Oguntade 

Vincent Bui 

## Homework 

[Hw Syntax for JAY](https://docs.google.com/document/d/11JuiHaq0C5c0OkRa0FFgYsvuvdOBDdsv/edit)

---

### To Compile the Scanner: 

```bash
java ScannerDemo
java ScannerDemo scan_valid.jay
```

---

### To Compile the Parser: 

```bash
- java ParserDemo
- java ParserDemo scan_valid.jay
```

---

### To create a sample file: 

Simply (edit or delete) the current scan_valid.jay file. If it does not exist, then once you clone it locally to test, simply add it and write a program in JAY. Make sure to follow JAY's rules. 

---

### To run a bunch of tests: 


```bash
for f in *.jay; do
  [ -e "$f" ] || continue
  echo "=== $f ==="
  java ParserDemo "$f" || echo "Exited with error on $f"
```


