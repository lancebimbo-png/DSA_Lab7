# Agent Log — Lab 7: HashMap and Key–Value Structures

**Date of work:** 2026-11-15
**Project folder:** `starter-kit/labs/07-hashmap-key-value/`
**Workbook:** DSA-WB-07 — `Lab_07_HashMap_KeyValue_Workbook.docx` (read in full)
**Course:** Data Structures and Algorithms (DSA) — CpE 211, Computer/Electronics Engineering
**Instructor:** Engr. Jamie Eduardo Rosal, MSCpE
**Assistant:** OpenCode
**Workflow:** Plan → Test → Build → Audit

**Current checkpoint:** Plan approved by the student; AGENT_LOG.md content finalized
in the Plan report but **not yet written to disk**. Build **NOT** authorized.
Test **NOT** authorized.

> **Reading note:** §1–§13 are the Plan record, finalized in the Plan report on
> 2026-11-15 and written to disk when the Test checkpoint begins. §14 onward will be
> appended at the Test, Build, and Audit checkpoints. Later sections supersede
> earlier sections' *status* statements; the decisions in §3 remain binding unless a
> later section explicitly revises one.

---

## 1. Assignment understanding

### 1.1 Scope in force

Both halves of the workbook are in scope. Part B is the primary engineering
acceptance contract; Part A is not omitted.

| Part | Requirement (workbook) | Implemented in | Tested in |
|---|---|---|---|
| A1 | Task 1 — `HashMap<String,Double>` of student grades: put, get, containsKey, remove | `src/HashMapBasics.java` | `test/LabTestRunner.java` (Part A smoke checks) |
| A2 | Task 2 — iterate with for-each over `.entrySet()` | `src/HashMapBasics.java`, `src/ProductInventory.java#displayStock()` | Part A smoke checks; `test/ProductInventoryTest.java` |
| A3 | Task 3 — `HashMap<String,Student>`: add, update, retrieve, display all | `src/HashMapBasics.java` | Part A smoke checks |
| A4 | Task 4 — mini-application: Product Code (String) → Quantity (Integer); add, update stock, display all | `src/ProductInventory.java` | `test/ProductInventoryTest.java` |
| A5 | Challenge Question — how HashMap handles two keys with the same hash code (collision & chaining) | written answer: README "Collision handling", REFLECTION §1 | n/a (written) |
| B1 | Add unique student IDs; **reject duplicate keys** | `src/StudentDirectory.java#addStudent` | `LabTestRunner` case 2; `StudentDirectoryTest` A1–A15 |
| B2 | Retrieve, update, remove records by ID | `StudentDirectory#getStudent/#updateStudent/#removeStudent` | `LabTestRunner` cases 3–5 |
| B3 | Return snapshots, never the mutable backing map | `StudentDirectory#snapshot` | `LabTestRunner` case 1; `StudentDirectoryTest` A3–A5, A14 |
| B4 | Character-frequency map + average vs. worst-case explanation | `src/CharacterFrequencyCounter.java` | `LabTestRunner` case 6; `CharacterFrequencyTest` F1–F10 |
| B5 | Automated tests, edge cases, complexity analysis, evidence | `test/*`; §8–§9 of this log | — |

### 1.2 Technical contract (workbook Part B, verbatim)

- "Add unique student IDs and reject duplicate keys."
- "Retrieve, update, and remove records by ID."
- "Return snapshots rather than exposing the mutable backing map."
- "Build a character-frequency map and explain average versus worst-case behavior."

Complexity contract (workbook Part B): put/get/remove O(1) average, O(n) worst;
snapshot O(n)/O(n); frequency count O(n) average, O(n²) pathological.

### 1.3 Learning outcomes to demonstrate

1. Implement a keyed student directory on a `HashMap` with correct duplicate,
   missing-key, update, removal and snapshot semantics.
2. Implement a character-frequency map and state precisely which characters it counts.
3. Design automated assertions for normal, boundary and invalid-operation cases
   covering the six-case acceptance matrix.
4. Connect predicted complexity to honestly measured evidence, separating average
   from worst case, and time from auxiliary space.
5. Demonstrate standard `HashMap` behaviour (Part A) while explaining how it differs
   from the stricter `StudentDirectory` contract (Part B).

---

## 2. Workspace inspection (Plan phase)

### 2.1 Findings

| Check | Result |
|---|---|
| Workspace root | `C:\Users\Lance\OneDrive\Documents\vscode\Lab7` (OneDrive reparse point) |
| Contents of `Lab7/` | **Empty — 0 files** at plan time |
| `starter-kit/labs/07-hashmap-key-value/` | **Never placed** in the workspace |
| `src/StudentDirectory.java` | **Not found** — full-profile filename sweep returned 0 hits |
| `test/LabTestRunner.java` | **Not found** — only the Lab 3 and Lab 8 runners exist, in sibling folders |
| Root `AGENTS.md` | **Not found.** The only AGENTS.md on disk is `Lab4\starter-kit\AGENTS.md`, **0 bytes** |
| Project `opencode.json` | **Not found.** The global `~\.config\opencode\opencode.jsonc` contains only `{"$schema": "https://opencode.ai/config.json"}` — no agents, permissions or rules |
| `.opencode/` agent directories | **None found** on the machine (either spelling) |
| `AGENT_LOG.md` / `REFLECTION.md` for Lab 7 | Not found |
| `run-tests.bat` / `run-tests.sh` for Lab 7 | Not found |
| Git repository for Lab 7 | Not found. No Git operations performed |
| GitHub | `lancebimbo-png` has DSA_Lab1…DSA_Lab6 and DSA_Lab8; **no DSA_Lab7** |
| Java toolchain | OpenJDK **25.0.5** Temurin LTS (`javac 25.0.5`); workbook requires **Java 17+** |
| Assignment document | Found: `Downloads\Lab_07_HashMap_KeyValue_Workbook.docx` (522,491 bytes) — read in full (395 extracted lines) |
| Prior-lab conventions inspected | Lab3 (`starter-kit/labs/03-linked-lists`: AGENT_LOG / REFLECTION / run-tests.bat / LabTestRunner / ComplexityMeasurement), Lab4 (starter-kit/AGENTS.md, AGENT_LOG.md), Lab6 (flat layout), Lab8 (`src/`+`test/`+`out/`+`run-tests.bat`+`AGENT_LOG.md`+`README.md`+`.gitignore`, `ComplexityAudit.java`) |
| OpenCode agent documentation | Verified at `https://opencode.ai/docs/agents/` — project agents live at `.opencode/agents/<name>.md` (**plural**); Markdown agents are auto-discovered and need **no** `opencode.json`; built-in agents are `build`, `plan`, `general` (subagent), `explore` (subagent) |

### 2.2 Implication

The supplied starter — including the "explicit TODO" the workbook expects to fail —
**was never placed in the workspace**, and no `DSA_Lab7` repository exists. The
project is therefore **scaffolded from the workbook's written contract after
explicit student approval**, the same precedent followed in Lab 8, whose README
records the equivalent situation and discloses it.

Consequently:

- `test/LabTestRunner.java` will be **agent-authored, not instructor-supplied**.
  Disclosed in README.md and in §2.2 and §11 of this log.
- There is no supplied test to weaken, delete, or bypass. The "test integrity"
  rule is satisfied trivially, but the **disclosure** obligation is real and met.
- The workbook's expectation that the runner "fail at an explicit TODO" is honoured
  through **temporary source stubs** (see §3.2): at the Test checkpoint the classes
  under test exist only as declarations and signatures whose bodies throw
  `UnsupportedOperationException`, so the suites genuinely fail before Build, and
  the exact failure output is pasted into §14.

### 2.3 Conventions adopted from prior labs

- `check(description, condition)` helper with `passed`/`failed` counters, ending in
  `throw new AssertionError(n + " test(s) failed")` (Lab 3 / Lab 8 pattern).
- Section-labelled output via a `section(String)` helper (Lab 8 pattern).
- Separate suites: acceptance runner, student-authored edge-case suites, complexity audit.
- `javac -d out src\*.java test\*.java` then `java -ea -cp out <Suite>`.
- `.gitignore` excluding `out/`, `*.class`, `.vscode/`.
- AGENT_LOG narrative sections; REFLECTION with an explicit "where the agent was
  wrong or limited" section (the workbook requires one independently evaluated
  agent limitation).

---

## 3. Approved design decisions (student-ratified, Plan checkpoint — unchanged)

| # | Decision | Rationale |
|---|---|---|
| D1 | **Duplicate student ID → reject, return `false`.** The existing student is **not** overwritten. | Part B's explicit requirement. A boolean result is directly assertable without exception handling. |
| D2 | **Missing student retrieval → `null`.** | Mirrors `HashMap.get`; keeps call sites simple. |
| D3 | **`Student` is a separate immutable Java record:** `record Student(String id, String name, double grade)` | Immutability makes the defensive snapshot a *deep* copy — no mutable `Student` is reachable from a returned map. Also satisfies "separate, clearly named classes". |
| D4 | **Directory holds a private `Map<String, Student>` backed by a `HashMap`.** | Satisfies "use a HashMap" while keeping the declared type interface-only. |
| D5 | **Snapshot returns a defensive `Map<String, Student>` copy**, never the backing map. | Part B: "return snapshots rather than exposing the mutable backing map". |
| D6 | **Frequency counter counts every character exactly as supplied** — uppercase and lowercase are **separate** keys; spaces and punctuation **are** counted. | The workbook states no filtering or case-folding rule; the "Repeated Characters" acceptance case must reflect raw input. The rule is documented and tested explicitly (F6, F7). |
| D7 | **Null student ID and null `Student` value are rejected.** All other null-input behaviour is chosen, documented and tested in §3.1. | Deterministic and exception-free for the caller where a return value suffices. |
| D8 | **A record whose `Student.id()` does not equal the map key is rejected.** | Prevents the key and the record's own id from diverging. |
| D9 | **Separate, clearly named classes** for directory, frequency counter and inventory functionality. | Rubric: "Cohesive classes, naming, and minimal duplication". |
| D10 | **Java 17-compatible syntax and APIs only.** | The workbook requires Java 17+; the installed JDK is 25. Portability must not depend on the newer toolchain. |

### 3.1 Null-input policy (complete, so nothing is left to chance)

| Call | Behaviour | Rationale |
|---|---|---|
| `addStudent(null, s)` | `false` (rejected) | D7 |
| `addStudent(id, null)` | `false` (rejected) | D7 |
| `addStudent(id, s)` where `s.id() == null` | `false` (rejected) | D8 — a null id cannot equal a non-null key |
| `addStudent(id, s)` where `s.id() != id` | `false` (rejected) | D8 |
| `addStudent(id, s)` where `s.name() == null` | **accepted** | No requirement forbids a null name and the workbook is silent, so no rule is invented. Documented and covered by test A12. |
| `getStudent(null)` | `null` | A null key is not present; consistent with D2 and with `HashMap.get`. No exception. |
| `updateStudent(null, s)` | `false` | D7 |
| `removeStudent(null)` | `false` | D7 |
| `containsStudent(null)` | `false` | D7 |
| `snapshot()` | never `null`; always a fresh, mutable, empty-safe copy | D5 |
| `count(null)` | throws `IllegalArgumentException` | There is no meaningful count; failing loudly beats silently returning an empty map. Asserted with a local throwable-capture helper (F9) — no JUnit dependency. |
| `count("")` | empty map (not `null`) | Boundary case. |
| `ProductInventory.addProduct(null, q)` | `false` | D7 analogue |
| `ProductInventory.updateStock(null, q)` / `adjustStock(null, d)` | `false` | D7 analogue |
| `ProductInventory.getQuantity(null)` | `0` | No product present. |

### 3.2 Test-checkpoint source stubs (temporary scaffolding only)

Because the instructor starter is missing, the Test checkpoint needs *something* to
compile against. The stubs are deliberately minimal and are **not** implementations.

**Permitted contents of a Test-checkpoint stub, and nothing else:**

1. The required class declaration (including the `record Student(String id, String
   name, double grade)` header — a declaration, with compiler-generated accessors).
2. The required method signatures, with exact return types and parameter types
   matching §3 and §5.
3. Method bodies that do nothing except `throw new UnsupportedOperationException(...)`.

**Explicitly prohibited during the Test checkpoint:**

- Any `HashMap` field, `Map` field, or collection instantiation.
- Any `put`, `get`, `remove`, `putIfAbsent`, `containsKey`, `entrySet`, or
  `getOrDefault` call.
- Any counting, copying, filtering, case-folding, or iteration logic.
- Any partially-correct implementation — a stub that returns a plausible value would
  be an implementation, and would corrupt the Test checkpoint's purpose.

**Stubs are replaced only during the separately approved Build checkpoint**, which
is where the bodies gain their real logic. The Test-checkpoint AGENT_LOG entry (§14)
will name every stub file explicitly and label it "temporary scaffolding — no
implementation".

---

## 4. Workbook conflicts and their resolution

**C1 — Duplicate keys: replace (Part A) vs. reject (Part B).**
Workbook §2.3 states "Keys must be unique. Adding a key that already exists will
replace its value" (standard `HashMap` behaviour), while Part B requires "Add unique
student IDs and reject duplicate keys."
**Resolution:** these govern different code. `StudentDirectory.addStudent` (Part B)
**rejects**. `HashMapBasics` (Part A) **demonstrates normal replacement** with a raw
`HashMap`, and the demo prints an explicit note contrasting the two, so the
divergence is visible rather than hidden. `ProductInventory` (Part A Task 4) mirrors
the Part B pattern: `addProduct` rejects an existing code, and `updateStock` /
`adjustStock` are the sanctioned ways to change a quantity.

**C2 — "Lab 8" legacy heading vs. "Lab 7" Part B heading.**
The workbook carries a legacy section titled "Lab 8: HashMap and Key-Value Data
Structures" while Part B and the workbook ID (`DSA-WB-07`) say Lab 7.
**Resolution:** this project is submitted as **Lab 7 — HashMap and Key–Value
Structures**, workbook ID **DSA-WB-07**. The legacy heading is noted once here so
its absence from the submission does not read as an error.

**C3 — Textbook worst case vs. modern `HashMap` implementation.**
The workbook gives O(n) worst case for put/get/remove and O(n²) pathological for
frequency counting. Since Java 8, `HashMap` **treeifies** a bin once it holds ≥ 8
entries — provided the table capacity is ≥ 64; below that capacity the map resizes
instead — bounding per-operation cost for a fully-colliding bin at **O(log n)** rather
than O(n).
**Resolution:** both are reported, separately and explicitly, and are never merged
into a single "the complexity is X" claim. The textbook bound is stated as the
worst case the workbook asks for; the treeified behaviour is stated as what Java 8+
actually does. Neither is presented as a correction of the other.

**C4 — The acceptance runner does not exist.**
**Resolution:** `test/LabTestRunner.java` is **agent-authored, not instructor-
supplied**. Disclosed in README.md, §2.2 and §11.

### 4.5 Script scope by stage

The run scripts are versioned by stage so that nothing is ever claimed to have run
that did not exist yet.

**Test-checkpoint version** (compiles the stubs + the four suites that exist; runs
those four suites only):

```bat
@echo off
setlocal
cd /d "%~dp0"
if exist out rmdir /s /q out
javac -d out src\*.java test\*.java
if errorlevel 1 exit /b 1
java -ea -cp out LabTestRunner
if errorlevel 1 exit /b 1
java -ea -cp out StudentDirectoryTest
if errorlevel 1 exit /b 1
java -ea -cp out CharacterFrequencyTest
if errorlevel 1 exit /b 1
java -ea -cp out ProductInventoryTest
if errorlevel 1 exit /b 1
echo All Test-stage suites completed.
exit /b 0
```

> **Expected at the Test checkpoint:** this script exits **non-zero**, because the
> stub bodies throw `UnsupportedOperationException`. That non-zero exit **is** the
> recorded evidence of the pre-implementation failure. It is not a defect to fix at
> this stage.

**Build-checkpoint additions:** `src/HashMapBasics.java`, `src/Lab7Demo.java`,
`test/ComplexityAudit.java` are created at Build, together with the real
implementations of the stubbed classes.

**Post-Build script version** (after the Build checkpoint is approved and verified,
the scripts are updated to the complete set):

```bat
@echo off
setlocal
cd /d "%~dp0"
if exist out rmdir /s /q out
javac -d out src\*.java test\*.java
if errorlevel 1 exit /b 1
java -ea -cp out LabTestRunner
if errorlevel 1 exit /b 1
java -ea -cp out StudentDirectoryTest
if errorlevel 1 exit /b 1
java -ea -cp out CharacterFrequencyTest
if errorlevel 1 exit /b 1
java -ea -cp out ProductInventoryTest
if errorlevel 1 exit /b 1
java -ea -cp out ComplexityAudit
if errorlevel 1 exit /b 1
java -cp out Lab7Demo
if errorlevel 1 exit /b 1
echo All test suites passed.
exit /b 0
```

`run-tests.sh` mirrors each version with `javac -d out src/*.java test/*.java` and
`java -ea -cp out <Suite>`.

---

## 5. Expected behavior

### 5.1 `StudentDirectory`

| Operation | Expected observable behavior |
|---|---|
| `addStudent(id, s)` on a fresh directory | Record stored under `id`; `size()` → 1; `getStudent(id)` returns a `Student` whose `id`, `name` and `grade` equal `s`'s. Returns `true`. |
| `addStudent(id, s)` with an **existing** `id` | **Rejected.** Returns `false`. The map is unchanged: `size()` unchanged, and `getStudent(id)` still returns the **original** record — the original `name` and `grade` are preserved. No exception. |
| `addStudent` with a null id, null record, null record-id, or mismatched record-id | Rejected, returns `false`, map unchanged, no exception. |
| `getStudent(id)` present | Returns the stored `Student`. |
| `getStudent(id)` absent (or null) | Returns `null`. No exception. Map unchanged. |
| `updateStudent(id, s)` present | The record is replaced. Returns `true`. `size()` is **unchanged**. `getStudent(id)` returns the new `name`/`grade`. All other records are untouched. |
| `updateStudent(id, s)` absent (or null id) | **No insertion.** Returns `false`. `size()` unchanged. `getStudent(id)` still `null`. |
| `removeStudent(id)` present | The entry is deleted. Returns `true`. `size()` decreases by exactly one. `getStudent(id)` → `null`. |
| `removeStudent(id)` absent (or null id) | No-op. Returns `false`. `size()` unchanged. |
| Any operation on an empty directory | Safe: no exception; `size()` stays 0. |
| `snapshot()` | Returns a **new** map with the same key→value pairs. Mutating the returned map (`put`, `remove`, `clear`) does **not** affect the directory. Mutating the directory afterwards does **not** change the already-returned snapshot. On an empty directory it returns an empty, **mutable** map — never `null`. |
| `size()` | The number of records. |
| `containsStudent(id)` | `true` iff `id` is a key. |

### 5.2 `CharacterFrequencyCounter`

| Input | Expected |
|---|---|
| `""` | Empty map, `size() == 0`; not `null`. |
| `"a"` | Exactly one entry: `a → 1`. |
| `"abracadabra"` | `{a→5, b→2, r→2, c→1, d→1}` — the **Repeated Characters** acceptance case. Total of all counts = 11 = input length. |
| `"aabb"` | `{a→2, b→2}` — two distinct keys, each count 2. |
| `"aaa"` | `{a→3}` — one distinct key, count 3. |
| `"Aa"` | **Two separate keys** (D6): uppercase and lowercase are counted independently. |
| `"a, b"` | `{a→1, ' '→1, ','→1, b→1}` — space and punctuation **are** counted (D6). |
| `null` | Throws `IllegalArgumentException`. |

### 5.3 `ProductInventory` (Part A Task 4)

| Operation | Expected |
|---|---|
| `addProduct(code, q)` on a fresh map | Stored; `size()` → 1; `getQuantity(code) == q`; returns `true`. |
| `addProduct(code, q)` with an existing code | **Rejected**, returns `false`; the stored quantity is unchanged. |
| `updateStock(code, q)` on an existing code | Quantity replaced with `q`; `size()` unchanged; returns `true`. |
| `updateStock(code, q)` on an absent code | No-op, returns `false`, nothing inserted. |
| `adjustStock(code, delta)` on an existing code | Quantity changed by `delta` (borrow/return semantics); returns `true`. |
| `getQuantity(code)` | The stored quantity, or `0` if absent. |
| `displayStock()` | Prints one `code -> quantity` line per entry, iterating with for-each over `entrySet()`. **Order is unspecified** and is never asserted. |
| Negative quantity on add/update | Rejected (`false`). Documented student decision: a stock level cannot be negative. |

### 5.4 `HashMapBasics` (Part A Tasks 1–3)

`runDemo()` performs, in order: put `Alice→89.5`, `Ben→91.0`, `Carla→85.5`; `get("Ben")`
→ `91.0`; `containsKey("Carla")` → `true`; `remove("Carla")` → `true`; a for-each over
`entrySet()` printing every remaining `name: grade`; then a `HashMap<String,Student>`
seeding `2025-001 → Student("2025-001","Alice",89.5)`, demonstrating **update by
replacement** on a duplicate `put`, retrieval by ID, and a final `entrySet()` dump of
all records. The demo prints an explicit note that raw `HashMap.put` **replaces**
while `StudentDirectory.addStudent` **rejects** — the C1 contrast.

---

## 6. Pseudocode

```java
// ---------- src/StudentDirectory.java ----------

addStudent(id, student):
    if id == null            -> return false          // D7: reject null ID
    if student == null       -> return false          // D7: reject null record
    if student.id() == null  -> return false          // D8: id must equal the key
    if student.id() != id    -> return false          // D8: id must equal the key
    if students.containsKey(id) -> return false       // D1: REJECT duplicate, do not overwrite
    students.put(id, student)                         // O(1) average
    return true

getStudent(id):
    if id == null            -> return null
    return students.get(id)                           // O(1) average; null when absent

updateStudent(id, student):
    if id == null or student == null -> return false
    if student.id() == null or student.id() != id -> return false
    if not students.containsKey(id) -> return false   // update must NOT insert
    students.put(id, student)                         // size() unchanged
    return true

removeStudent(id):
    if id == null            -> return false
    return students.remove(id)                        // O(1) average; false when absent

snapshot():
    copy = new HashMap<>(students)                    // O(n) time, O(n) auxiliary space
    return copy                                      // caller may mutate it freely

containsStudent(id):
    if id == null            -> return false
    return students.containsKey(id)

size():
    return students.size()

// ---------- src/CharacterFrequencyCounter.java ----------

count(text):
    if text == null -> throw new IllegalArgumentException("text must not be null")
    freq = new HashMap<>()
    for each char c in text:                           // n iterations, n = text.length()
        freq.put(c, freq.getOrDefault(c, 0) + 1)       // one map update per character
    return freq                                       // O(n) average; O(k) space, k = distinct chars

// ---------- src/ProductInventory.java ----------

addProduct(code, quantity):
    if code == null or quantity < 0 -> return false
    if stock.containsKey(code)      -> return false   // duplicate code rejected
    stock.put(code, quantity)
    return true

updateStock(code, quantity):
    if code == null or quantity < 0 -> return false
    if not stock.containsKey(code)  -> return false   // update must NOT insert
    stock.put(code, quantity)
    return true

adjustStock(code, delta):
    if code == null or not stock.containsKey(code) -> return false
    newQty = stock.get(code) + delta
    if newQty < 0 -> return false                     // cannot go negative
    stock.put(code, newQty)
    return true

getQuantity(code):
    if code == null -> return 0
    qty = stock.get(code)
    return (qty == null) ? 0 : qty

displayStock():
    for each entry in stock.entrySet():                // for-each over entrySet(), A2
        print(entry.getKey() + " -> " + entry.getValue())
```

---

## 7. Edge cases and test plan

### 7.1 The six mandatory acceptance cases (`test/LabTestRunner.java`)

| # | Case | Setup | Exact assertions | Passes because / fails if |
|---|---|---|---|---|
| 1 | **Empty Map** | fresh `StudentDirectory`; `count("")` | `size()==0`; `snapshot().isEmpty()`; `getStudent("X")==null`; `removeStudent("X")==false`; `updateStudent("X",s)==false`; `containsStudent("X")==false`; `count("")` non-null and empty | An empty structure must be queryable without throwing, and a failed operation must not create state. |
| 2 | **Duplicate Key** | `addStudent("A", alice)` then `addStudent("A", bob)` | first → `true`; second → **`false`**; `size()==1`; **`getStudent("A").name()=="Alice"`**; **`getStudent("A").grade()==89.5`** | The name/grade assertions are the point: a *replacing* implementation would also show `size()==1`, so only the content check proves rejection. |
| 3 | **Missing Key** | populated dir (3 students); `getStudent("ZZZ")`, `updateStudent("ZZZ",s)`, `removeStudent("ZZZ")` | `getStudent("ZZZ")==null`; `updateStudent("ZZZ",s)==false`; `removeStudent("ZZZ")==false`; `size()==3`; `getStudent("ZZZ")==null` after the failed update | Missing-key operations must be safe no-ops — not insertions, not exceptions. |
| 4 | **Record Update** | dir with Alice/Bob/Carla; `updateStudent("B", bobNew)` | → `true`; `getStudent("B").name()=="Robert"`; `getStudent("B").grade()==95.0`; `size()==3` (**unchanged**); `getStudent("A").name()=="Alice"`; `getStudent("C").grade()==85.5` | Update replaces in place without appending and without disturbing neighbours. |
| 5 | **Removal** | dir with 3 students; `removeStudent("B")` then `removeStudent("B")` again | first → `true`; `size()==2`; `getStudent("B")==null`; `getStudent("A")!=null`; `getStudent("C")!=null`; second → `false`; `size()==2` | Removal decrements exactly once and is idempotent-safe. |
| 6 | **Repeated Characters** | `count("abracadabra")` | `size()==5`; `get('a')==5`; `get('b')==2`; `get('r')==2`; `get('c')==1`; `get('d')==1`; **sum of all counts == 11**; every count ≥ 1 | The sum assertion is what catches an off-by-one in repeated counting — the most likely bug in this routine. |

### 7.2 Additional tests — Part B (`test/StudentDirectoryTest.java`, `test/CharacterFrequencyTest.java`)

| # | Behaviour | Assertions | Fails if |
|---|---|---|---|
| A1 | Normal insertion and retrieval | add 1 student → `size()==1`, `getStudent(id)!=null`, id/name/grade all equal | retrieval returns the wrong record or `null` |
| A2 | Multiple records coexist | add 5 students → `size()==5`; all 5 retrievable with correct fields; all 5 ids distinct | records overwrite one another, or one is lost |
| A3 | **Snapshot independence — mutate the copy** | `snap.put("X",z); snap.remove("A");` → `dir.size()==2`; `dir.getStudent("A")!=null`; `dir.getStudent("X")==null` | the backing map leaks to the caller |
| A4 | **Snapshot independence — mutate the directory afterwards** | `snap = dir.snapshot(); dir.removeStudent("A");` → `snap` still contains "A"; `snap.size()==2` | the snapshot is a live view rather than a copy |
| A5 | Snapshot of an empty directory | `snap.isEmpty()` **and** the returned map is mutable | `null` is returned, or an immutable map is returned |
| A6 | Update targets only the requested id | after `updateStudent("A",…)`, `getStudent("B")` name and grade unchanged | the update clobbers the wrong entry |
| A7 | Update of a nonexistent id does not insert | `updateStudent("ZZZ",s)==false`; `size()` unchanged; `getStudent("ZZZ")==null` | the update silently upserts |
| A8 | Remove nonexistent is a no-op | `removeStudent("ZZZ")==false`; `size()` unchanged | removal adds or corrupts state |
| A9 | Null id rejected | `addStudent(null, s)==false`; `size()==0`; no exception | an NPE leaks to the caller |
| A10 | Null record rejected | `addStudent("A", null)==false`; `size()==0`; no exception | a null record is stored |
| A11 | Id/key mismatch rejected | `addStudent("A", new Student("B",…))==false`; `size()==0` | the key and the record's own id diverge |
| A12 | Null record name accepted (documented rule) | `addStudent("A", new Student("A", null, 90.0))==true`; `getStudent("A").name()==null` | the documented null-name rule is violated |
| A13 | Null-safe reads | `getStudent(null)==null`; `updateStudent(null,s)==false`; `removeStudent(null)==false`; `containsStudent(null)==false`; `size()` unchanged | null inputs cause an exception or a mutation |
| A14 | Snapshot content matches the directory | snapshot of a 3-student dir has `size()==3` and exactly the same key set | the snapshot is incomplete or stale |
| A15 | `containsStudent` agrees with `getStudent` | for each of 3 ids, `containsStudent(id) == (getStudent(id)!=null)` | the two accessors disagree |
| F1 | Empty input to the counter | `count("")` non-null, empty, `size()==0` | `null` or an exception on empty input |
| F2 | Single-character input | `count("x").size()==1`; `get('x')==1` | off-by-one on the trivial case |
| F3 | Repeated character only | `count("aaa").get('a')==3`; `size()==1` | distinct characters are counted instead of occurrences |
| F4 | Mixed character set | `count("aabb")` → a=2, b=2, `size()==2` | a character is skipped or merged |
| F5 | Mid-string repeat | `count("xyzzy")` → y=2, x=1, z=2, `size()==3` | a repeat in the middle is mishandled |
| F6 | **Case sensitivity (D6)** | `count("Aa")` → `size()==2`; `get('A')==1`; `get('a')==1` | case is folded, or one of the two keys is lost |
| F7 | **Whitespace and punctuation are counted (D6)** | `count("a, b")` → `size()==4`; `get(' ')==1`; `get(',')==1` | whitespace/punctuation is filtered out |
| F8 | Counts sum to the input length | `"the quick brown fox"` → sum == 19, 16 distinct keys (15 letters + space), `' '`==3, `'o'`==2 | a character is dropped or double-counted |
| F9 | Null input to the counter throws | local throwable-capture helper asserts `java.lang.IllegalArgumentException` | null is silently accepted, or a different exception type is thrown |
| F10 | Non-ASCII character is one distinct key | `count("aé")` → `size()==2`; `get('a')==1`; `get('é')==1` | multi-byte handling corrupts counts |

**Test-integrity statement.** No supplied test exists, so none can be weakened,
deleted or bypassed. The six matrix cases live in `LabTestRunner`; the edge cases
live in separate suites so the matrix can be re-run in isolation. Every snapshot
and frequency-map assertion compares as a **Map/Set**, never as an ordered `List`
— `HashMap` iteration order is unspecified and must never be asserted.

### 7.3 Additional tests — Part A

`test/LabTestRunner.java` carries three Part A **smoke checks** written directly
against a raw `HashMap` and the `Student` record (no `HashMapBasics` dependency, so
they compile at the Test checkpoint):

| # | Behaviour | Assertions | Fails if |
|---|---|---|---|
| S1 | Part A raw-`HashMap` **replacement** semantics (C1 contrast) | put "Alice" twice → `size()==3`, `get("Alice")==95.0`; `get`/`containsKey`/`remove` behave | the documented Part A behaviour is not demonstrated |
| S2 | Part A `entrySet()` iteration (Task 2) and Student storage (Task 3) | `entrySet().size()==2`; each key resolves to its stored record; each value carries the matching ID; for-each visits every entry | entries are not iterable via `entrySet()` |
| S3 | `Student` record semantics | accessors equal; two equal instances are `equals` and share a `hashCode` | the record is not a correct immutable value type |

`test/ProductInventoryTest.java` carries the inventory behaviour:

| # | Behaviour | Assertions | Fails if |
|---|---|---|---|
| P1 | Product added | `addProduct("P1",5)==true`; `getQuantity("P1")==5`; `size()==1` | the product is not stored |
| P2 | Duplicate product code rejected | `addProduct("P1",9)==false`; `getQuantity("P1")==5`; `size()==1` | the quantity is silently replaced |
| P3 | Stock update (absolute) | `updateStock("P1",12)==true`; `getQuantity("P1")==12`; `size()==1` | the update does not take effect, or appends |
| P4 | Update of an absent code does not insert | `updateStock("P9",3)==false`; `size()==1` | it upserts |
| P5 | Borrow / return (delta) | `adjustStock("P1",-2)==true` → 10; `adjustStock("P1",5)==true` → 15 | the delta is applied incorrectly |
| P6 | Borrow below zero rejected | `adjustStock("P1",-999)==false`; `getQuantity("P1")==15` | the stock goes negative |
| P7 | Missing product quantity | `getQuantity("NOPE")==0` | a missing product reports a nonzero quantity |
| P8 | Negative quantity rejected | `addProduct("P2",-1)==false`; `updateStock("P1",-1)==false`; `size()==1` | an invalid stock level is stored |
| P9 | `displayStock()` covers every entry | capture stdout; assert one `code -> qty` line per entry | an entry is skipped or malformed |
| P10 | Null product code rejected | `addProduct(null,5)==false`; `updateStock(null,5)==false`; `adjustStock(null,1)==false`; `getQuantity(null)==0`; `size()==0` | an NPE leaks, or null is stored |

### 7.4 Expected failure at the Test checkpoint

Before Build, `src/` contains only the stub classes described in §3.2. Each suite
records the **actual** failure output: the mandatory behaviours are reported as
`[FAIL]` with the blocking `UnsupportedOperationException` message printed, and each
suite ends with a non-zero exit via `throw new AssertionError(...)`. The Part A
smoke checks (S1–S3) exercise plain `java.util.HashMap` and the `Student` record
directly, so they are expected to **pass** at the Test checkpoint — which
demonstrates that those assertions are meaningful and independent of the stubs.
That output is the recorded evidence the workbook asks for and is pasted verbatim
into §14.

---

## 8. Predicted complexity

### 8.1 Time

| Operation | Average case | Worst case (textbook, per workbook) | Behaviour on Java 8+ (treeified bins) |
|---|---|---|---|
| `addStudent` — put, new key | **O(1)** amortized | O(n) | O(log n) |
| `addStudent` — duplicate rejected | O(1) | O(n) | O(log n) |
| `getStudent` | **O(1)** | O(n) | O(log n) |
| `updateStudent` | **O(1)** | O(n) | O(log n) |
| `removeStudent` | **O(1)** | O(n) | O(log n) |
| `snapshot()` | **O(n)** | O(n) | O(n) |
| `count(text)` | **O(n)** | **O(n²)** | O(n log k) |
| `size()`, `containsStudent` | O(1) | O(1) | O(1) |
| `ProductInventory` operations | O(1) average | O(n) | O(log n) |

**"Average"** assumes a well-distributed hash: expected constant bucket occupancy
per operation. **"Worst case"** assumes all keys hash into one bucket: the bin
holds a chain, and each operation traverses it.

**The Java 8+ nuance, stated explicitly (C3):** `HashMap` treeifies a bin when it
reaches **8** entries, provided the table capacity is at least **64**; below that
capacity the map resizes instead. A treeified bin is a red-black tree, so
per-operation cost for a fully-colliding key set is bounded at **O(log n)** rather
than O(n). The textbook O(n) worst case therefore describes the pre-Java-8
implementation. Both are reported; neither is presented as a correction of the other.

**Honest caveat on the O(n²) pathological bound for frequency counting:** the
counter's key type is `Character`, whose `hashCode()` **is** the character's code
point. For any realistic alphabet the distinct characters have distinct code points
and therefore distinct hash codes, so **a fully-colliding character set cannot be
constructed with real `Character` keys**. The O(n²) bound is a *theoretical* worst
case inherited from the general HashMap analysis; it is **not exercisable** here,
and §9 explicitly does not attempt to measure it. The "one map update per
character" mechanism it derives from is stated, and its actual cost is measured.

### 8.2 Auxiliary space (separate from time)

| Structure | Auxiliary space |
|---|---|
| `StudentDirectory` backing map | **O(m)** where m = number of students |
| Each `snapshot()` handed out | **O(m)** — a full copy; unavoidable, and it is space *plus* time, both linear |
| `CharacterFrequencyCounter` result | **O(k)** where k = number of distinct characters, independent of input length n |
| `ProductInventory` backing map | **O(p)** where p = number of product codes |
| `HashMapBasics` maps | **O(m)** for the grade map, **O(s)** for the student map |

Note the asymmetry worth defending verbally: **counting frequency is O(n) time but
O(k) space** — a 1,000,000-character sentence uses the same map as a 26-character
one — whereas a **snapshot is O(n) time *and* O(n) space**.

---

## 9. Complexity measurement plan

### 9.1 The four quantities, kept separate

These are never combined into a single derived number, and each is labelled as
what it actually is.

| # | Quantity | What it is | What it is **not** |
|---|---|---|---|
| 1 | **API-level operation counts** | The number of method invocations **my own code issues** on the `Map`: `putCalls`, `getCalls`, `removeCalls`, `containsCalls`, `charactersProcessed`, `mapUpdateCalls`, `snapshotEntriesCopied`, `distinctKeys` | **Not** a count of internal HashMap operations |
| 2 | **Internal HashMap operations** | Hash recomputations, bucket probes, chain/tree traversals, resizes | **Not directly counted** by the counters in (1) — they are internal to `HashMap` and not observable from outside. The audit prints an explicit note saying so. |
| 3 | **Elapsed time** | `System.nanoTime()` around a block of operations, converted to ms. Labelled: *elapsed wall-clock time including JVM and runtime overhead* | **Not** an exact measure of algorithmic steps and **not** a count of internal operations. |
| 4 | **Auxiliary space** | Entry counts as the space evidence: `snapshotEntriesCopied` (= m), `distinctKeys` (= k). A heap-delta reading may be printed as a clearly-labelled secondary observation only | Never used as a substitute for (3), and never mixed with it |

### 9.2 Input sizes and shapes — measured separately

**A. `StudentDirectory` — size sweep:** n = 1,000 / 10,000 / 100,000 students.

**B. `StudentDirectory` — shape sweep at n = 100,000**, using **real `String` IDs**
(no wrapper class, no instrumentation of `src/`):

| Shape | Key construction | Purpose |
|---|---|---|
| **S1 distributed** | `"S" + String.format("%06d", i)` | natural `String.hashCode()` spread — the average case |
| **S2 shared-prefix** | `"2025-BSCPE-" + String.format("%06d", i)` | long common prefix, varying suffix — a realistic but more clustered shape |
| **S3 hash-colliding** | a small search helper that groups candidate IDs by `hashCode()` and selects the largest group, producing **genuinely equal-hash `String`s** | the pathological shape, using the same key type the class actually uses |

**C. `CharacterFrequencyCounter` — length × shape sweep:**

| Input | Length | Shape |
|---|---|---|
| `"a"` | 1 | single character, 1 distinct |
| 26-letter alphabet, repeated | 1,000 / 100,000 / 1,000,000 | 26 distinct characters |
| `"a" × n` | same lengths | 1 distinct character |
| mixed text with spaces and punctuation | same lengths | exercises the D6 counting rule |

### 9.3 Predictions recorded before measuring — not guarantees

The following are the predictions held **before** any measurement is run. They are
recorded so that the actual result can be *compared* with them, not so that the
result can be forced to match them.

1. **S1 and S2:** per-operation elapsed time is expected to stay roughly flat as n
   grows 10×, consistent with amortized O(1).
2. **S3 (colliding):** per-operation elapsed time is expected to be measurably
   **higher** than S1 at the same n, but the degradation is expected to be **mild
   rather than linear**, because the bin is treeified at 8 entries on Java 8+.
   The textbook O(n) figure is the pre-Java-8 bound.
3. **Counter:** elapsed time is expected to grow linearly with text length, while
   `distinctKeys` stays at 26 regardless of length, and `charactersProcessed`
   equals the length exactly.
4. **No collision measurement is attempted for `Character` keys** — see §8.1. This
   is recorded as a limitation of the measurement, not as an untested claim.

**Explicitly: no outcome is guaranteed.** The audit prints whatever it measures;
this log records the actual numbers. If the measurement disagrees with a
prediction, the disagreement is reported and analysed — it becomes the primary
finding, not a footnote. If a difference is too small to resolve at this machine's
resolution, that is reported as **inconclusive**, not as a match.

### 9.4 Protocol

- A **discarded warm-up phase** (≥ 2 full repetitions) precedes every reported
  figure — a lesson already documented in Lab 3's REFLECTION.md, where the first
  loop was skewed by JIT warm-up.
- Report the **median of ≥ 5 repetitions**, never a single reading.
- Warm-up figures are printed separately and labelled as warm-up.
- Operation counts, elapsed times and space counts are printed in **separate
  columns** and never combined.

### 9.5 Non-fabrication clause

No numeric result appears in this plan. Every figure will come from actually
running `ComplexityAudit` on this machine and will be pasted **verbatim** into
this log and into REFLECTION.md, together with the exact command and the raw
output. If a measurement disagrees with §9.3, the disagreement is reported and
explained rather than re-measured until the numbers look convenient.

---

## 10. Plan agent challenge and response

| # | Challenge | Disposition |
|---|---|---|
| C-1 | **The API is inferred from prose, not supplied.** If the instructor's starter used different names, the suite would target the wrong API. | **Accepted as an irreducible risk** (§2.1). Mitigation: §3 is recorded as an explicitly student-ratified decision set, and README.md will state the project was scaffolded from the workbook contract. |
| C-2 | **The Duplicate-Key case is the easiest to write wrong** — `size()==1` also holds under a replacing implementation. | **Accepted and corrected.** Case 2 asserts the original `name` **and** `grade` survive (§7.1). |
| C-3 | **Snapshot independence has two directions; only one is usually tested.** | **Accepted.** A3 and A4 test opposite directions. |
| C-4 | **Snapshot depth** — a mutable `Student` would be reachable from the returned map. | **Accepted and resolved by design.** D3's immutable `record` makes the defensive copy deep. |
| C-5 | **`HashMap` iteration order is unspecified** — ordered-list assertions are flaky. | **Accepted.** All snapshot/frequency assertions compare as Map/Set; P9 asserts printed *content*, not order. |
| C-6 | **`double` equality** — `0.1` is not binary-exact. | **Accepted.** All fixture grades use binary-exact literals (89.5, 91.0, 85.5, 95.0, 12.0). |
| C-7 | **`containsKey` + `put` is two lookups with a window between them.** | **Accepted.** The implementation will use `students.putIfAbsent(id, student) == null` — one lookup, no window, duplicate rejection impossible to mis-wire. Equivalent to §6; the equivalence is noted at Build. |
| C-8 | **A raw `HashMap` accepts a null key.** | **Accepted.** D7 + §3.1 reject it; A9/A13 test it. |
| C-9 | **A weak "Empty Map" case.** | **Accepted.** Case 1 asserts all four no-op operations. |
| C-10 | **`updateStudent` implemented as `put` would upsert.** | **Accepted.** A7 asserts it does not insert. |
| C-11 | **The workbook's audit agents do not exist in this environment.** | **Accepted; deferred by student instruction.** §11 records them as unavailable. The decision to create them or to use the available general agent is explicitly **postponed to the Audit checkpoint**. |
| C-12 | **The brief instructed me to read a root `AGENTS.md`, `opencode.json` and `.opencode/` — none exist.** | **Accepted.** §2.1 records this as a finding rather than claiming rules were read. |
| C-13 | **JDK 25 installed vs. Java 17+ required.** | **Accepted.** D10 restricts to Java 17-compatible syntax and APIs; Build additionally compiles with `--release 17` to *prove* portability. |
| C-14 | **`javac -d out src/*.java test/*.java` relies on javac's own wildcard expansion, unverified here.** | **Accepted.** Verified as the first action of the Test checkpoint; documented fallback is `Get-ChildItem src,test -Filter *.java` expansion. `run-tests.bat` uses `src\*.java` inside `cmd.exe`, as the sibling labs do. |
| C-15 | **Part A Task 4's "update stock levels" is ambiguous** — absolute set or relative delta. | **Accepted; resolved by implementing both.** `updateStock(code, newQuantity)` (absolute) and `adjustStock(code, delta)` (relative, covering the workbook's Problem 2 borrow/return). Both tested (P3, P5, P6). |
| C-16 | **An agent-authored acceptance suite is not independent evidence.** | **Accepted.** Disclosed in README.md, §2.2 and §11; the six matrix cases are isolated in `LabTestRunner`; edge cases live in separate student-reviewed suites. |
| C-17 | **The frequency tests are all lower-case ASCII.** | **Accepted.** F6 and F7 pin D6 down explicitly, with the rule restated in a comment on each. |
| C-18 | **A pathological-shape claim needs a real measurement, not a wrapper class.** | **Accepted — this drove the approved correction.** The wrapper approach was discarded. Shape S3 uses genuinely equal-hash `String` IDs discovered by search, so the key type matches the class under test; no collision measurement is attempted for `Character` keys. |
| C-19 | **Part A asks to "display" results, which needs an entry point, but the required commands only run `LabTestRunner`.** | **Accepted.** `src/Lab7Demo.java` provides the Part A demonstration `main` — a **Build-stage** file, added to the scripts only in the post-Build script version (§4.5). |
| C-20 | **A test script that names suites which do not yet exist would misrepresent what ran.** | **Accepted — this drove the approved correction.** §4.5 now version-scripts by stage, and §14 will record exactly which suites existed and ran at each checkpoint. |
| C-21 | **JUnit's `assertThrows` is unavailable — the labs use plain `javac`/`java` with no dependencies.** | **Accepted.** F9 uses a small local `captureThrowable(Runnable)` helper that returns the thrown class name, giving the same assertion with no external library. |

---

## 11. Tool and agent availability (honest record)

| Item | Status | Effect on the workflow |
|---|---|---|
| Instructor starter-kit for lab 07 | **Absent.** Never placed in `Lab7/`; `StudentDirectory.java` and `LabTestRunner.java` are nowhere on the machine; no `DSA_Lab7` repository exists. | Project scaffolded from the workbook contract (§2.2), with the runner disclosed as agent-authored. |
| Root `AGENTS.md` | **Absent** for this project. The only one on disk (`Lab4\starter-kit\AGENTS.md`) is **0 bytes**. | No project-local agent rules are in force. Nothing is claimed as obeyed. |
| Project `opencode.json` / `opencode.jsonc` | **Absent.** The global `~\.config\opencode\opencode.jsonc` contains only `{"$schema": "https://opencode.ai/config.json"}`. | No project permissions or rules apply. **No new `opencode.json`/`opencode.jsonc` will be created merely to define Markdown agents** — per the OpenCode agent documentation, Markdown agents are auto-discovered without one. |
| `.opencode/` agent directory | **None exists** on this machine, under either spelling. | — |
| `@test-reviewer` | **NOT AVAILABLE.** It exists only as a workspace-scoped Markdown agent at `.opencode/agents/test-reviewer.md`; no such file exists. The installed OpenCode exposes the built-ins `build`, `plan`, `general` (subagent) and `explore` (subagent) only — there is no built-in `test-reviewer`. | **Recorded as unavailable.** By student instruction, **no agent files are created now.** The decision to create them or to use the available `general` agent is deferred to the **Audit checkpoint**. |
| `@complexity-auditor` | **NOT AVAILABLE.** Same situation — no built-in with this name, and no `.opencode/agents/complexity-auditor.md`. | **Recorded as unavailable**, same deferral as above. |
| If still absent at the Audit checkpoint | — | I perform both audits with the available `general` subagent and record their absence in this log as the **"one independently evaluated agent limitation"** the workbook requires (Lab 8 precedent). |
| Java toolchain | OpenJDK 25.0.5 Temurin present; `javac`/`java` resolve. | D10 restricts the code to Java 17-compatible syntax and APIs; `--release 17` compilation at Build proves it. |
| Git | Not a repository. **No Git operations performed**, and none are planned without a separate instruction. | — |
| Session mode at the time of the Test request | The session was still in **Plan** mode, whose `edit` permission is denied for all project paths. | The Test checkpoint could not be executed until the mode is switched to **Build/Edit**. The complete file contents were preserved in the session plan directory so nothing was lost. |

---

## 12. Final project structure and file inventory

```
Lab7/                                      [workspace root]
├── starter-kit/
│   └── labs/
│       └── 07-hashmap-key-value/         [PROJECT ROOT — all lab files live here]
│           ├── src/
│           │   ├── Student.java                     [TEST]   record declaration only — no bodies
│           │   ├── StudentDirectory.java            [TEST stub → BUILD impl]
│           │   ├── CharacterFrequencyCounter.java   [TEST stub → BUILD impl]
│           │   ├── ProductInventory.java            [TEST stub → BUILD impl]
│           │   ├── HashMapBasics.java               [BUILD]  Part A Tasks 1–3
│           │   └── Lab7Demo.java                    [BUILD]  Part A runnable demo (main)
│           ├── test/
│           │   ├── LabTestRunner.java               [TEST]   6 mandatory cases + Part A smoke S1–S3
│           │   ├── StudentDirectoryTest.java        [TEST]   student-authored Part B edge cases A1–A15
│           │   ├── CharacterFrequencyTest.java      [TEST]   frequency edge cases F1–F10
│           │   ├── ProductInventoryTest.java        [TEST]   inventory behaviour P1–P10
│           │   └── ComplexityAudit.java             [BUILD]  timing + API-level operation counts
│           ├── out/                                 [generated]  excluded by .gitignore
│           ├── AGENT_LOG.md                         [TEST]   written at Test start with §1–§13; §14+ appended per checkpoint
│           ├── README.md                            [FINAL]  API, commands, disclosure, collision-handling explanation
│           ├── REFLECTION.md                        [FINAL]  learning, agent limitation, predicted-vs-measured, checklist
│           ├── .gitignore                           [TEST]   out/, *.class, .vscode/
│           ├── run-tests.bat                        [TEST v1 → updated post-Build]
│           └── run-tests.sh                         [TEST v1 → updated post-Build]
└── .opencode/                                       [DEFERRED to the Audit checkpoint]
    └── agents/                                      [NOT CREATED NOW]
        ├── test-reviewer.md
        └── complexity-auditor.md
```

**Stage summary**

| Stage | Files created | Files modified | Files run |
|---|---|---|---|
| **Plan** | none — AGENT_LOG.md content finalized in the Plan report only | none | none |
| **Test** | `src/Student.java`, `src/StudentDirectory.java` *(stub)*, `src/CharacterFrequencyCounter.java` *(stub)*, `src/ProductInventory.java` *(stub)*, `test/LabTestRunner.java`, `test/StudentDirectoryTest.java`, `test/CharacterFrequencyTest.java`, `test/ProductInventoryTest.java`, `AGENT_LOG.md` *(§1–§13 + §14)*, `.gitignore`, `run-tests.bat` *(v1)*, `run-tests.sh` *(v1)* | none | the **four** Test-stage suites only — expected to **fail** (non-zero exit) against the stubs |
| **Build** | `src/HashMapBasics.java`, `src/Lab7Demo.java`, `test/ComplexityAudit.java` | the three stub files get their real bodies; `AGENT_LOG.md` §15 | all suites + complexity audit + Part A demo, after the scripts are updated to v2 |
| **Audit** | `.opencode/agents/test-reviewer.md`, `.opencode/agents/complexity-auditor.md` **only if the student approves at that point** | `AGENT_LOG.md` §16 | re-verification of the full suite |
| **Final** | `README.md`, `REFLECTION.md` | `AGENT_LOG.md` §17 | final full-suite run |

---

## 13. Workflow status and next checkpoint

| Checkpoint | Status |
|---|---|
| **Plan** | **Complete.** Approved by the student. AGENT_LOG.md content finalized in the Plan report; **not yet written to disk**. Build **NOT** authorized. Test **NOT** authorized. |
| **Test** | **Pending.** The session was in Plan mode when the Test checkpoint was approved, whose `edit` permission denies every project path. The complete file contents were preserved in the session plan directory so the checkpoint can be executed mechanically once the mode is switched. |
| **Build** | **Not authorized.** Would implement the 3 stubbed classes, add `HashMapBasics.java`, `Lab7Demo.java`, `ComplexityAudit.java`, and replace the stub bodies only at that point. |
| **Audit** | Pending. Decision on creating `.opencode/agents/` deferred to this checkpoint. |
| **Git** | Not started. No Git operations performed. |

---

## 14. TEST checkpoint (executed)

**Checkpoint date:** 2026-11-15
**Mode:** Build (the session was still in Plan mode when the Test checkpoint was
approved; the mode was switched to Build and the checkpoint was then executed).
**Authorization:** Test checkpoint only. Build and Audit NOT authorized.

### 14.1 Files created at this checkpoint

Twelve files were created. None existed before this checkpoint.

| Path | Kind | Purpose |
|---|---|---|
| `AGENT_LOG.md` | document | §1–§13 (Plan record) written to disk, plus this §14 |
| `.gitignore` | config | `out/`, `*.class`, `.vscode/` |
| `run-tests.bat` | script | Test-stage v1 — compiles and runs ONLY the four suites that exist |
| `run-tests.sh` | script | Test-stage v1, POSIX equivalent |
| `src/Student.java` | **declaration only** | `public record Student(String id, String name, double grade) {}` — compiler-generated accessors, no hand-written body |
| `src/StudentDirectory.java` | **STUB — no implementation** | declarations and signatures only; every body throws `UnsupportedOperationException` |
| `src/CharacterFrequencyCounter.java` | **STUB — no implementation** | declarations and signatures only; every body throws `UnsupportedOperationException` |
| `src/ProductInventory.java` | **STUB — no implementation** | declarations and signatures only; every body throws `UnsupportedOperationException` |
| `test/LabTestRunner.java` | test | acceptance runner: the 6 mandatory matrix cases + Part A smoke checks S1–S3 |
| `test/StudentDirectoryTest.java` | test | student-authored Part B edge cases A1–A15 |
| `test/CharacterFrequencyTest.java` | test | frequency-counter edge cases F1–F10 |
| `test/ProductInventoryTest.java` | test | Part A inventory behaviour P1–P10 |

**Stub discipline verification.** The three stub files contain **no** `HashMap` or
`Map` field, **no** collection instantiation, **no** `put` / `get` / `remove` /
`putIfAbsent` / `containsKey` / `entrySet` / `getOrDefault` call, and **no**
counting, copying, filtering, case-folding or iteration logic. Each stub body is
exactly `throw new UnsupportedOperationException("<signature> - not implemented
(Test checkpoint stub)");`. These bodies are replaced only during the separately
approved Build checkpoint.

### 14.2 Exact commands run

```
javac -d out src\*.java test\*.java
java -ea -cp out LabTestRunner
java -ea -cp out StudentDirectoryTest
java -ea -cp out CharacterFrequencyTest
java -ea -cp out ProductInventoryTest
run-tests.bat
```

All run from `starter-kit/labs/07-hashmap-key-value/` on
OpenJDK 25.0.5 Temurin (JDK 25 installed; the source targets Java 17-compatible
syntax and APIs per D10).

### 14.3 Compile output (verbatim)

```
> javac -d out src\*.java test\*.java
javac exit code: 0
```

Compilation succeeded with **no errors and no warnings**.

### 14.4 `run-tests.bat` (Test-stage v1) — verbatim output and exit code

```
> run-tests.bat
=== LabTestRunner (agent-authored acceptance suite) ===
Source: acceptance-test matrix, Lab_07_HashMap_KeyValue_Workbook.docx (DSA-WB-07), Part B
DISCLOSURE: agent-authored, not instructor-supplied - the starter kit was never placed in the workspace.

-- Empty Map --
  [FAIL] empty-map API implemented
       blocked by: size() - not implemented (Test checkpoint stub)
-- Duplicate Key --
  [FAIL] duplicate-key rejection implemented
       blocked by: addStudent(String, Student) - not implemented (Test checkpoint stub)
-- Missing Key --
  [FAIL] missing-key behaviour implemented
       blocked by: addStudent(String, Student) - not implemented (Test checkpoint stub)
-- Record Update --
  [FAIL] record-update behaviour implemented
       blocked by: addStudent(String, Student) - not implemented (Test checkpoint stub)
-- Removal --
  [FAIL] removal behaviour implemented
       blocked by: addStudent(String, Student) - not implemented (Test checkpoint stub)
-- Repeated Characters --
  [FAIL] character-frequency counting implemented
       blocked by: count(String) - not implemented (Test checkpoint stub)
-- Part A smoke - raw HashMap replacement semantics (Task 1) --
  [PASS] raw HashMap replaces an existing key instead of adding one
  [PASS] raw HashMap keeps the last value written
  [PASS] raw HashMap get returns a stored value
  [PASS] raw HashMap containsKey finds a stored key
  [PASS] raw HashMap remove deletes the entry
-- Part A smoke - entrySet iteration (Task 2) and Student storage (Task 3) --
  [PASS] entrySet exposes every entry
  [PASS] entry key resolves to its stored record
  [PASS] entry value carries the matching student ID
  [PASS] entry key resolves to its stored record
  [PASS] entry value carries the matching student ID
  [PASS] for-each over entrySet visited every entry
-- Part A smoke - Student record semantics (Task 3) --
  [PASS] record exposes its id
  [PASS] record exposes its name
  [PASS] record exposes its grade
  [PASS] records with equal components are equal
  [PASS] equal records share a hash code

LabTestRunner results: 16 passed, 6 failed
Exception in thread "main" java.lang.AssertionError: 6 acceptance test(s) failed
	at LabTestRunner.main(LabTestRunner.java:47)

---- run-tests.bat exit code: 1 ----
```

The script stops at the first failing suite (`if errorlevel 1 exit /b 1`), which is
the intended fail-fast behaviour. The three remaining suites were therefore run
individually, below, so that every suite's actual output is on record.

### 14.5 `StudentDirectoryTest` — verbatim output and exit code

```
> java -ea -cp out StudentDirectoryTest
=== StudentDirectoryTest (student-authored edge cases) ===

-- A1 - normal insertion and retrieval --
  [FAIL] normal insertion and retrieval implemented
       blocked by: addStudent(String, Student) - not implemented (Test checkpoint stub)
-- A2 - multiple records coexist --
  [FAIL] multiple-record storage implemented
       blocked by: addStudent(String, Student) - not implemented (Test checkpoint stub)
-- A3 - snapshot is isolated when the copy is mutated --
  [FAIL] snapshot isolation implemented
       blocked by: addStudent(String, Student) - not implemented (Test checkpoint stub)
-- A4 - snapshot is isolated from later directory changes --
  [FAIL] snapshot independence implemented
       blocked by: addStudent(String, Student) - not implemented (Test checkpoint stub)
-- A5 - snapshot of an empty directory is empty and mutable --
  [FAIL] empty-directory snapshot implemented
       blocked by: snapshot() - not implemented (Test checkpoint stub)
-- A6 - update touches only the targeted ID --
  [FAIL] targeted update implemented
       blocked by: addStudent(String, Student) - not implemented (Test checkpoint stub)
-- A7 - update of a missing ID does not insert --
  [FAIL] update-without-insert implemented
       blocked by: addStudent(String, Student) - not implemented (Test checkpoint stub)
-- A8 - remove of a missing ID is a no-op --
  [FAIL] remove-no-op implemented
       blocked by: addStudent(String, Student) - not implemented (Test checkpoint stub)
-- A9 - a null student ID is rejected --
  [FAIL] null-ID rejection implemented
       blocked by: addStudent(String, Student) - not implemented (Test checkpoint stub)
-- A10 - a null Student value is rejected --
  [FAIL] null-Student rejection implemented
       blocked by: addStudent(String, Student) - not implemented (Test checkpoint stub)
-- A11 - a Student whose ID differs from the key is rejected --
  [FAIL] ID-consistency rejection implemented
       blocked by: addStudent(String, Student) - not implemented (Test checkpoint stub)
-- A12 - a Student with a null name is accepted (documented rule) --
  [FAIL] null-name acceptance implemented
       blocked by: addStudent(String, Student) - not implemented (Test checkpoint stub)
-- A13 - null inputs are safe for read and delete operations --
  [FAIL] null-safe reads implemented
       blocked by: addStudent(String, Student) - not implemented (Test checkpoint stub)
-- A14 - snapshot content matches the directory --
  [FAIL] snapshot content implemented
       blocked by: addStudent(String, Student) - not implemented (Test checkpoint stub)
-- A15 - containsStudent agrees with getStudent --
  [FAIL] containsStudent agreement implemented
       blocked by: addStudent(String, Student) - not implemented (Test checkpoint stub)

StudentDirectoryTest results: 0 passed, 15 failed
Exception in thread "main" java.lang.AssertionError: 15 edge-case test(s) failed
	at StudentDirectoryTest.main(StudentDirectoryTest.java:41)

---- exit code: 1 ----
```

### 14.6 `CharacterFrequencyTest` — verbatim output and exit code

```
> java -ea -cp out CharacterFrequencyTest
=== CharacterFrequencyTest (student-authored edge cases) ===

-- F1 - empty input yields an empty map --
  [FAIL] empty-input handling implemented
       blocked by: count(String) - not implemented (Test checkpoint stub)
-- F2 - single-character input --
  [FAIL] single-character handling implemented
       blocked by: count(String) - not implemented (Test checkpoint stub)
-- F3 - one character repeated --
  [FAIL] repeated-character counting implemented
       blocked by: count(String) - not implemented (Test checkpoint stub)
-- F4 - mixed character set --
  [FAIL] mixed-character counting implemented
       blocked by: count(String) - not implemented (Test checkpoint stub)
-- F5 - a repeat in the middle of the string --
  [FAIL] mid-string repeat counting implemented
       blocked by: count(String) - not implemented (Test checkpoint stub)
-- F6 - uppercase and lowercase are separate keys (rule D6) --
  [FAIL] case-sensitive counting implemented
       blocked by: count(String) - not implemented (Test checkpoint stub)
-- F7 - spaces and punctuation are counted (rule D6) --
  [FAIL] whitespace and punctuation counting implemented
       blocked by: count(String) - not implemented (Test checkpoint stub)
-- F8 - the counts sum to the input length --
  [FAIL] count total implemented
       blocked by: count(String) - not implemented (Test checkpoint stub)
-- F9 - null input is rejected --
  [FAIL] count(null) throws IllegalArgumentException
       actually threw: java.lang.UnsupportedOperationException
-- F10 - a non-ASCII character is one distinct key --
  [FAIL] non-ASCII counting implemented
       blocked by: count(String) - not implemented (Test checkpoint stub)

CharacterFrequencyTest results: 0 passed, 10 failed
Exception in thread "main" java.lang.AssertionError: 10 edge-case test(s) failed
	at CharacterFrequencyTest.main(CharacterFrequencyTest.java:37)

---- exit code: 1 ----
```

### 14.7 `ProductInventoryTest` — verbatim output and exit code

```
> java -ea -cp out ProductInventoryTest
=== ProductInventoryTest (Part A mini-application) ===

-- P1 - a product is added --
  [FAIL] product insertion implemented
       blocked by: addProduct(String, int) - not implemented (Test checkpoint stub)
-- P2 - a duplicate product code is rejected --
  [FAIL] duplicate-code rejection implemented
       blocked by: addProduct(String, int) - not implemented (Test checkpoint stub)
-- P3 - updateStock sets an absolute quantity --
  [FAIL] stock update implemented
       blocked by: addProduct(String, int) - not implemented (Test checkpoint stub)
-- P4 - updateStock on an absent code does not insert --
  [FAIL] update-without-insert implemented
       blocked by: addProduct(String, int) - not implemented (Test checkpoint stub)
-- P5 - adjustStock applies a delta (borrow / return) --
  [FAIL] delta adjustment implemented
       blocked by: addProduct(String, int) - not implemented (Test checkpoint stub)
-- P6 - a borrow that would go negative is rejected --
  [FAIL] negative-stock guard implemented
       blocked by: addProduct(String, int) - not implemented (Test checkpoint stub)
-- P7 - an absent product reports a zero quantity --
  [FAIL] absent-product lookup implemented
       blocked by: getQuantity(String) - not implemented (Test checkpoint stub)
-- P8 - a negative quantity is rejected --
  [FAIL] negative-quantity rejection implemented
       blocked by: addProduct(String, int) - not implemented (Test checkpoint stub)
-- P9 - displayStock prints one line per entry --
  [FAIL] displayStock implemented
       blocked by: addProduct(String, int) - not implemented (Test checkpoint stub)
-- P10 - a null product code is rejected --
  [FAIL] null-code rejection implemented
       blocked by: addProduct(String, int) - not implemented (Test checkpoint stub)

ProductInventoryTest results: 0 passed, 10 failed
Exception in thread "main" java.lang.AssertionError: 10 Part A test(s) failed
	at ProductInventoryTest.main(ProductInventoryTest.java:39)

---- exit code: 1 ----
```

### 14.8 Result summary (actual, measured on this machine)

| Suite | Passed | Failed | Exit code | Expected at this checkpoint? |
|---|---|---|---|---|
| `LabTestRunner` | 16 | 6 | 1 | Yes — 6 mandatory matrix cases FAIL against the stubs |
| `StudentDirectoryTest` | 0 | 15 | 1 | Yes — all 15 Part B edge cases FAIL against the stub |
| `CharacterFrequencyTest` | 0 | 10 | 1 | Yes — all 10 frequency cases FAIL against the stub |
| `ProductInventoryTest` | 0 | 10 | 1 | Yes — all 10 Part A inventory cases FAIL against the stub |
| **Total** | **16** | **41** | — | — |

### 14.9 Reading of the result

**This non-zero failure is the expected and required outcome of the Test
checkpoint.** The workbook requires that the suite fail before any implementation
is written, and it does: 41 assertions fail, every one of them blocked by a stub
throwing `UnsupportedOperationException`.

**Why exactly 16 assertions pass.** The 16 passing assertions are the three Part A
smoke checks (S1–S3), which exercise plain `java.util.HashMap` and the `Student`
record directly and therefore do not touch the stubs. They are the control group
that proves the harness itself works: the runner, the `check()` helper, the
pass/fail counters and the `Student` record are all functional, so the 41 failures
are caused by the absent implementation and by nothing else.

**F9 deserves a note.** `count(null)` currently throws
`java.lang.UnsupportedOperationException`; the assertion expects
`java.lang.IllegalArgumentException`. The suite printed the actual type
(`actually threw: java.lang.UnsupportedOperationException`) instead of failing
opaquely, so the exact gap is visible and will be closed by the Build checkpoint.

**Test-integrity statement.** No assertion was weakened, removed, skipped or
bypassed to produce this result. The four suites contain **222 assertion call
sites** at this point (see §14.11.4). **57 results were reported** — 16 passed and
41 failed. The remaining call sites were *not reached*: each failing behaviour
aborts at its first call into the stub, and the `catch (UnsupportedOperationException)`
block reports that whole behaviour as a single failure. The 41 failures correspond
exactly to the 41 behaviours whose implementation does not yet exist, and the 16
passes are the three Part A smoke behaviours, which never touch the stubs. The
distinction between "assertion call sites present" and "results reported at the
Test checkpoint" is stated here because conflating the two would overstate how
much of the suite has actually been exercised at this stage.

### 14.10 What was deliberately NOT done at this checkpoint

- No implementation logic was written. The three stub classes still contain only
  `UnsupportedOperationException` bodies; `Student.java` is a bare record header.
- `src/HashMapBasics.java`, `src/Lab7Demo.java` and `test/ComplexityAudit.java`
  were **not** created — they are Build-stage files.
- The run scripts were **not** updated to the post-Build version; they still run
  only the four suites that exist. `ComplexityAudit` and `Lab7Demo` are not
  referenced by them, so nothing was claimed to have run that did not.
- No custom agent files and no `opencode.json` / `opencode.jsonc` were created.
- **No Git operations were performed** — no `init`, `add`, `commit`, `push`,
  `branch`, `clone`, and no GitHub repository was created. The project directory is
  not a Git repository.
- `out/` was generated by the compiler and is excluded by `.gitignore`.

### 14.11 Test-checkpoint corrections (review-driven, executed)

**Date:** 2026-11-15, after the student reviewed the Test-checkpoint result.
**Scope:** test files only. No source file, no stub body, no design decision and
no approved-plan file was changed.

#### 14.11.1 Correction 1 — `test/CharacterFrequencyTest.java`, `testCountsSumToInputLength()` (F8)

**Defect found.** The expected distinct-character count for `"the quick brown
fox"` was written as **15**. That number is the count of distinct *letters* only.
Because rule D6 counts spaces as characters, the correct count of distinct
**characters** is **16** (15 distinct letters + 1 distinct space).

**Verification performed before editing.** The string was tallied
character-by-character rather than by eye:

```
input: 'the quick brown fox'  length = 19
distinct characters = 16
  ' ' x3
  'b' x1  'c' x1  'e' x1  'f' x1  'h' x1  'i' x1  'k' x1  'n' x1
  'o' x2  'q' x1  'r' x1  't' x1  'u' x1  'w' x1  'x' x1

letters only (distinct) = 15
distinct spaces = 1
```

**A second and third defect surfaced from the same tally.** The two neighbouring
assertions were also numerically wrong and would have failed against a *correct*
implementation:

| Assertion | Was | Is | Why |
|---|---|---|---|
| distinct characters | 15 | **16** | 15 letters + 1 space |
| `' '` occurrences | 2 | **3** | after "the", after "quick", after "brown" |
| `'o'` occurrences | 3 | **2** | once in "brown", once in "fox" |
| total counted | 19 | 19 | 14x1 + 2 + 3 = 19 — unchanged and still correct |

All three were corrected. None was removed or weakened — the assertions still
exist, still compare against a concrete expected value, and the corrected values
are the true ones. The tally is now written into the test as a comment so the
derivation can be defended live:

```java
// "the quick brown fox" is 19 characters:
//   t h e _ q u i c k _ b r o w n _ f o x
// 15 distinct letters (t,h,e,q,u,i,c,k,b,r,o,w,n,f,x) + 1 distinct space = 16 distinct.
// The space occurs 3x (after "the", "quick", "brown"); the letter 'o' occurs 2x
// (in "brown" and in "fox"); every other character occurs once.
// 14x1 + 2 + 3 = 19 characters counted in total.
check("the counts sum to 19", total == 19);
check("the string has 16 distinct characters (15 letters + 1 space)", freq.size() == 16);
check("' ' occurs three times", freq.get(' ') == 3);
check("'o' occurs two times", freq.get('o') == 2);
```

**Lesson recorded.** The original expected value was computed as "distinct
letters" and then applied to a "distinct characters" assertion. A single
off-by-one in the *unit* of the count, not in the arithmetic. The tally is now
derived from the string itself rather than assumed.

#### 14.11.2 Correction 2 — `test/ProductInventoryTest.java`, `testNegativeQuantityIsRejected()` (P8)

**Defect found.** The original P8 called `updateStock("P1", -1)` while **no product
"P1" existed**. The call would return `false`, but for the *wrong reason* — the
absent-code path — so the assertion proved nothing about the negative-quantity
rule it claimed to test. A test that can pass for the wrong reason is not a test.

**Correction.** Product `"P1"` with quantity `5` is now added first, so the
negative update is attempted against an **existing** product:

```java
// P1 is added FIRST so that updateStock("P1", -1) exercises the negative-quantity
// guard on an EXISTING product, rather than the absent-code path (which would
// also return false, but for the wrong reason and would prove nothing about
// the negative-quantity rule).
check("adding P2 with a negative quantity returns false", !inv.addProduct("P2", -1));
check("adding P1 with quantity 5 returns true", inv.addProduct("P1", 5));
check("updating the existing P1 to a negative quantity returns false",
        !inv.updateStock("P1", -1));
check("P1's quantity remains 5", inv.getQuantity("P1") == 5);
check("P2 was not stored", inv.getQuantity("P2") == 0);
check("inventory size is exactly 1", inv.size() == 1);
```

**Note on "the inventory holds nothing".** The original third assertion asserted
`size() == 0`. Adding "P1" makes that value false by construction, so keeping it
verbatim would have planted a deliberately-failing assertion. It was therefore
**corrected in place**, not deleted: the same behaviour is now asserted as
`inv.size() == 1` — "the inventory holds only the valid product" — which is
strictly stronger, because the rejected product "P2" must also be absent. A
separate assertion (`getQuantity("P2") == 0`) pins that down directly.

**Net effect.** P8 went from 3 assertions to 6, and every one of them now tests
the behaviour its description claims. The original negative-addition assertion was
kept unchanged.

#### 14.11.3 Re-compile and re-run (actual output, exit codes)

```
> javac -d out src\*.java test\*.java
javac exit code: 0

############ java -ea -cp out CharacterFrequencyTest ############
=== CharacterFrequencyTest (student-authored edge cases) ===

-- F1 - empty input yields an empty map --
  [FAIL] empty-input handling implemented
       blocked by: count(String) - not implemented (Test checkpoint stub)
-- F2 - single-character input --
  [FAIL] single-character handling implemented
       blocked by: count(String) - not implemented (Test checkpoint stub)
-- F3 - one character repeated --
  [FAIL] repeated-character counting implemented
       blocked by: count(String) - not implemented (Test checkpoint stub)
-- F4 - mixed character set --
  [FAIL] mixed-character counting implemented
       blocked by: count(String) - not implemented (Test checkpoint stub)
-- F5 - a repeat in the middle of the string --
  [FAIL] mid-string repeat counting implemented
       blocked by: count(String) - not implemented (Test checkpoint stub)
-- F6 - uppercase and lowercase are separate keys (rule D6) --
  [FAIL] case-sensitive counting implemented
       blocked by: count(String) - not implemented (Test checkpoint stub)
-- F7 - spaces and punctuation are counted (rule D6) --
  [FAIL] whitespace and punctuation counting implemented
       blocked by: count(String) - not implemented (Test checkpoint stub)
-- F8 - the counts sum to the input length --
  [FAIL] count total implemented
       blocked by: count(String) - not implemented (Test checkpoint stub)
-- F9 - null input is rejected --
  [FAIL] count(null) throws IllegalArgumentException
       actually threw: java.lang.UnsupportedOperationException
-- F10 - a non-ASCII character is one distinct key --
  [FAIL] non-ASCII counting implemented
       blocked by: count(String) - not implemented (Test checkpoint stub)

CharacterFrequencyTest results: 0 passed, 10 failed
Exception in thread "main" java.lang.AssertionError: 10 edge-case test(s) failed
	at CharacterFrequencyTest.main(CharacterFrequencyTest.java:37)
---- exit code: 1 ----

############ java -ea -cp out ProductInventoryTest ############
=== ProductInventoryTest (Part A mini-application) ===

-- P1 - a product is added --
  [FAIL] product insertion implemented
       blocked by: addProduct(String, int) - not implemented (Test checkpoint stub)
-- P2 - a duplicate product code is rejected --
  [FAIL] duplicate-code rejection implemented
       blocked by: addProduct(String, int) - not implemented (Test checkpoint stub)
-- P3 - updateStock sets an absolute quantity --
  [FAIL] stock update implemented
       blocked by: addProduct(String, int) - not implemented (Test checkpoint stub)
-- P4 - updateStock on an absent code does not insert --
  [FAIL] update-without-insert implemented
       blocked by: addProduct(String, int) - not implemented (Test checkpoint stub)
-- P5 - adjustStock applies a delta (borrow / return) --
  [FAIL] delta adjustment implemented
       blocked by: addProduct(String, int) - not implemented (Test checkpoint stub)
-- P6 - a borrow that would go negative is rejected --
  [FAIL] negative-stock guard implemented
       blocked by: addProduct(String, int) - not implemented (Test checkpoint stub)
-- P7 - an absent product reports a zero quantity --
  [FAIL] absent-product lookup implemented
       blocked by: getQuantity(String) - not implemented (Test checkpoint stub)
-- P8 - a negative quantity is rejected --
  [FAIL] negative-quantity rejection implemented
       blocked by: addProduct(String, int) - not implemented (Test checkpoint stub)
-- P9 - displayStock prints one line per entry --
  [FAIL] displayStock implemented
       blocked by: addProduct(String, int) - not implemented (Test checkpoint stub)
-- P10 - a null product code is rejected --
  [FAIL] null-code rejection implemented
       blocked by: addProduct(String, int) - not implemented (Test checkpoint stub)

ProductInventoryTest results: 0 passed, 10 failed
Exception in thread "main" java.lang.AssertionError: 10 Part A test(s) failed
	at ProductInventoryTest.main(ProductInventoryTest.java:39)
---- exit code: 1 ----
```

**Regression check on the two unmodified suites** (re-run after the recompile, to
prove the corrections did not disturb anything else):

```
############ java -ea -cp out LabTestRunner ############
LabTestRunner results: 16 passed, 6 failed
Exception in thread "main" java.lang.AssertionError: 6 acceptance test(s) failed
---- exit code: 1 ----

############ java -ea -cp out StudentDirectoryTest ############
StudentDirectoryTest results: 0 passed, 15 failed
Exception in thread "main" java.lang.AssertionError: 15 edge-case test(s) failed
---- exit code: 1 ----
```

**Stub-discipline re-verification after the corrections** (proving `src/` was not
touched):

```
CharacterFrequencyCounter.java: throw-bodies = 1, non-stub lines = 0
ProductInventory.java:           throw-bodies = 6, non-stub lines = 0
Student.java:                    throw-bodies = 0, non-stub lines = 0
StudentDirectory.java:           throw-bodies = 7, non-stub lines = 0
```

Every method body in the three stub classes is still exactly
`throw new UnsupportedOperationException(...)`, and `Student.java` is still a bare
record header. No implementation was written at this checkpoint.

#### 14.11.4 Assertion accounting after the corrections

Two different numbers are involved and they must not be conflated:

- **Assertion call sites** = the number of `check(...)` calls written into the four
  suite files. Counted mechanically, not by hand.
- **Results reported at the Test checkpoint** = the number of `[PASS]`/`[FAIL]`
  lines actually printed. A behaviour whose first call throws never reaches its
  remaining assertions, so this is much smaller.

| Suite | Assertion call sites before | after | Reported at Test checkpoint |
|---|---|---|---|
| `LabTestRunner` | 62 | 62 | 16 passed / 6 failed |
| `StudentDirectoryTest` | 80 | 80 | 0 passed / 15 failed |
| `CharacterFrequencyTest` | 39 | 39 | 0 passed / 10 failed |
| `ProductInventoryTest` | 41 | **44** | 0 passed / 10 failed |
| **Total** | **222** | **225** | **16 passed / 41 failed = 57 reported** |

Counting method (reproducible) — this counts **individual `check(...)` calls**,
not matching lines:

```powershell
# line count (what the first draft reported)
(Select-String -Path test/<Suite>.java -Pattern 'check\("' -AllMatches | Measure-Object).Count
# individual match count (the correct method)
(Select-String -Path test/<Suite>.java -Pattern 'check\("' -AllMatches |
  ForEach-Object { $_.Matches.Count } | Measure-Object -Sum).Sum
```

The first draft of this log reported only the line count. The line count and the
individual-match count turn out to be **identical for all four suites** (62 / 80 /
39 / 44), because every `check(` call in these files sits on its own line — this
was verified explicitly by scanning for any line carrying more than one `check("`
occurrence, and none was found. The totals are therefore **unchanged**, but the
method recorded here is now the rigorous one, so the number no longer depends on
that incidental formatting fact. See §14.11.5 for the verification run.

**Net effect of the corrections: +3 assertion call sites (222 → 225), none removed.**
All +3 come from P8 (3 assertions → 6). The three corrected expected values in F8
change no counts — only the values the assertions compare against.

The per-suite *reported failure* counts are unchanged (10 and 10) because at the
Test checkpoint each behaviour still fails at its first call into the stub, so P8
still contributes exactly one reported failure regardless of how many assertions it
contains. The added assertions become live at the Build checkpoint.

#### 14.11.5 Second review round — P8 re-verification and counting-method fix

**Date:** 2026-11-15, after a further student review of P8 and of the counting
method. **Scope:** `test/ProductInventoryTest.java` and this log only.

##### 14.11.5.1 The two obsolete checks named in the review were already absent

The review asked to remove (a) a check calling `updateStock("P1", -1)` *before* "P1"
exists, and (b) a check asserting the inventory is empty *before* "P1" is added.
Both were already removed in the first correction round (§14.11.2), and this was
verified by enumerating every relevant call site in the file:

```
=== every updateStock / addProduct / size()==0 occurrence in ProductInventoryTest.java ===
  line  47: check("adding a product returns true", inv.addProduct("P1", 5));            <- P1
  line  60: inv.addProduct("P1", 5);                                                    <- P2
  line  61: check("adding the same code again returns false", !inv.addProduct("P1", 9));<- P2
  line  74: inv.addProduct("P1", 5);                                                    <- P3
  line  75: check("updating the stock returns true", inv.updateStock("P1", 12));         <- P3
  line  88: inv.addProduct("P1", 5);                                                    <- P4
  line  89: check("updating an absent code returns false", !inv.updateStock("P9", 3));  <- P4
  line 102: inv.addProduct("P1", 12);                                                   <- P5
  line 117: inv.addProduct("P1", 15);                                                   <- P6
  line 141: // P1 is added FIRST so that updateStock("P1", -1) exercises ...            <- P8 comment
  line 145: check("adding P2 with a negative quantity returns false", !inv.addProduct("P2", -1));  <- P8
  line 146: check("adding P1 with quantity 5 returns true", inv.addProduct("P1", 5));   <- P8
  line 148:     !inv.updateStock("P1", -1));                                            <- P8, AFTER P1 exists
  line 164: inv.addProduct("P1", 5);                                                    <- P9
  line 165: inv.addProduct("P2", 9);                                                    <- P9
  line 189: check("adding a null code returns false", !inv.addProduct(null, 5));        <- P10
  line 190: check("updating a null code returns false", !inv.updateStock(null, 5));     <- P10
  line 193: check("the inventory holds nothing", inv.size() == 0);                      <- P10
```

The only `updateStock("P1", -1)` in the file is at **line 148**, which executes
*after* `addProduct("P1", 5)` at **line 146**. There is no `size() == 0` assertion
anywhere inside P8.

**The one remaining `"the inventory holds nothing", inv.size() == 0` is at line
193, inside `testNullProductCodeIsRejected()` (P10), not P8.** In P10 no product
is ever added — every call uses a `null` code and is rejected — so `size() == 0` is
the *correct* assertion there. It was deliberately **kept**: deleting it would
have removed a valid assertion belonging to a different behaviour, which the
instruction not to weaken or remove assertions forbids.

##### 14.11.5.2 What actually changed in P8 this round

The behaviour and the order were already correct. To remove any residual
ambiguity for the live check, the six assertion **descriptions** were rewritten to
match the required wording one-for-one, so the test now reads exactly as
specified:

| # | Before (round 1) | After (round 2) |
|---|---|---|
| 1 | "adding a negative quantity returns false" | "adding P2 with a negative quantity returns false" |
| 2 | "adding a product with a valid quantity returns true" | "adding P1 with quantity 5 returns true" |
| 3 | "updating an existing product to a negative quantity returns false" | "updating the existing P1 to a negative quantity returns false" |
| 4 | "the existing quantity is unchanged after the rejected update" | "P1's quantity remains 5" |
| 5 | "the product rejected at addition time was not stored" | "P2 was not stored" |
| 6 | "the inventory holds only the valid product" | "inventory size is exactly 1" |

The order of execution is unchanged and is now: add P2(-1) → add P1(5) →
update P1(-1) → verify P1 == 5 → verify P2 == 0 → verify size == 1.

**No assertion was added or removed.** The count in P8 remains **6**.

##### 14.11.5.3 Typo fixed in this log

The code block quoted in §14.11.2 contained the word **"negative-quantency"**
(typo). The Java source file never contained the typo — only the quoted block in
this log did. The block has been corrected to "negative-quantity". A project-wide
scan now returns exactly **two** occurrences of the typo string, and **both are
inside this subsection** — the line above, which names it, and the sentence
immediately below, which refers to it. **Every `.java` file in the project
contains zero occurrences**, and no source file was ever affected.

##### 14.11.5.4 Counting method re-verified (rigorous)

```
=== RIGOROUS assertion count: sum of individual matches ===
  LabTestRunner            lines= 62  individual check() calls= 62  identical
  StudentDirectoryTest     lines= 80  individual check() calls= 80  identical
  CharacterFrequencyTest   lines= 39  individual check() calls= 39  identical
  ProductInventoryTest     lines= 44  individual check() calls= 44  identical
  TOTAL                                                     225
```

A separate scan for any line carrying more than one `check("` occurrence returned
nothing, which is why the line count and the individual-match count coincide.
**The totals in §14.11.4 are confirmed correct and unchanged: 225 assertion call
sites in total, 222 before the first correction round, +3 from P8.**

##### 14.11.5.5 Re-compile and re-run (actual output, exit codes)

```
> javac -d out src\*.java test\*.java
javac exit code: 0

> java -ea -cp out ProductInventoryTest
=== ProductInventoryTest (Part A mini-application) ===

-- P1 - a product is added --
  [FAIL] product insertion implemented
       blocked by: addProduct(String, int) - not implemented (Test checkpoint stub)
-- P2 - a duplicate product code is rejected --
  [FAIL] duplicate-code rejection implemented
       blocked by: addProduct(String, int) - not implemented (Test checkpoint stub)
-- P3 - updateStock sets an absolute quantity --
  [FAIL] stock update implemented
       blocked by: addProduct(String, int) - not implemented (Test checkpoint stub)
-- P4 - updateStock on an absent code does not insert --
  [FAIL] update-without-insert implemented
       blocked by: addProduct(String, int) - not implemented (Test checkpoint stub)
-- P5 - adjustStock applies a delta (borrow / return) --
  [FAIL] delta adjustment implemented
       blocked by: addProduct(String, int) - not implemented (Test checkpoint stub)
-- P6 - a borrow that would go negative is rejected --
  [FAIL] negative-stock guard implemented
       blocked by: addProduct(String, int) - not implemented (Test checkpoint stub)
-- P7 - an absent product reports a zero quantity --
  [FAIL] absent-product lookup implemented
       blocked by: getQuantity(String) - not implemented (Test checkpoint stub)
-- P8 - a negative quantity is rejected --
  [FAIL] negative-quantity rejection implemented
       blocked by: addProduct(String, int) - not implemented (Test checkpoint stub)
-- P9 - displayStock prints one line per entry --
  [FAIL] displayStock implemented
       blocked by: addProduct(String, int) - not implemented (Test checkpoint stub)
-- P10 - a null product code is rejected --
  [FAIL] null-code rejection implemented
       blocked by: addProduct(String, int) - not implemented (Test checkpoint stub)

ProductInventoryTest results: 0 passed, 10 failed
Exception in thread "main" java.lang.AssertionError: 10 Part A test(s) failed
	at ProductInventoryTest.main(ProductInventoryTest.java:39)
---- exit code: 1 ----
```

**Regression check on the three unmodified suites** (re-run after the recompile):

```
  LabTestRunner            -> 16 passed, 6 failed   (exit 1)
  StudentDirectoryTest     -> 0 passed, 15 failed   (exit 1)
  CharacterFrequencyTest   -> 0 passed, 10 failed   (exit 1)
```

**Stub discipline re-verified** — `src/` mtimes are unchanged at 21:25:27–21:25:32,
i.e. no source file was touched during either correction round:

```
  CharacterFrequencyCounter.java   21:25:32
  ProductInventory.java            21:25:32
  Student.java                     21:25:27
  StudentDirectory.java            21:25:32
```

### 14.12 Status

| Checkpoint | Status |
|---|---|
| Plan | Complete (§1–§13, on disk) |
| **Test** | **Complete, including review corrections.** 12 files created; compile clean; **225 assertion call sites present, 57 results reported (16 passed / 41 failed)**; all four suites exit 1 as expected against the stubs. |
| Build | **Not authorized.** Next: replace the three stub bodies with real logic, add `HashMapBasics.java`, `Lab7Demo.java` and `ComplexityAudit.java`, then update the scripts to the post-Build version. |
| Audit | Not started. |
| Git | Not started. No Git operations performed. |

---

## 15. BUILD checkpoint (executed)

**Checkpoint date:** 2026-11-15
**Authorization:** Build checkpoint only — implementation and build verification.
Audit NOT started. Git NOT started.

### 15.1 Build start

The three stub classes written at the Test checkpoint were read back before being
replaced (§1 of the Build instruction). Their signatures were preserved exactly;
only the bodies changed.

| Stub method signature | Preserved? |
|---|---|
| `boolean addStudent(String id, Student student)` | yes |
| `Student getStudent(String id)` | yes |
| `boolean updateStudent(String id, Student student)` | yes |
| `boolean removeStudent(String id)` | yes |
| `Map<String, Student> snapshot()` | yes |
| `int size()` | yes |
| `boolean containsStudent(String id)` | yes |
| `Map<Character, Integer> count(String text)` | yes |
| `boolean addProduct(String code, int quantity)` | yes |
| `boolean updateStock(String code, int quantity)` | yes |
| `boolean adjustStock(String code, int delta)` | yes |
| `int getQuantity(String code)` | yes |
| `void displayStock()` | yes |
| `int size()` (ProductInventory) | yes |

### 15.2 Files implemented and created

| File | Status | Purpose |
|---|---|---|
| `src/StudentDirectory.java` | **implemented** (stub replaced) | Part B core directory |
| `src/CharacterFrequencyCounter.java` | **implemented** (stub replaced) | Part B frequency counter |
| `src/ProductInventory.java` | **implemented** (stub replaced) | Part A Task 4 mini-application |
| `src/HashMapBasics.java` | **created** | Part A Tasks 1–3 |
| `src/Lab7Demo.java` | **created** | runnable Part A + Part B demonstration (`main`) |
| `src/ComplexityAudit.java` | **created** | complexity measurement harness |
| `run-tests.bat` | **updated** | now compiles everything and runs all four suites + audit + demo |
| `run-tests.sh` | **updated** | same, POSIX |
| `README.md` | **created** | run instructions and the workbook's required explanations |
| `src/Student.java` | unchanged | the `Student` record was already complete at the Test checkpoint |
| `test/*.java` | unchanged | **no test was modified at the Build checkpoint** |

**One location change from the approved inventory, made on the Build
instruction.** The approved Plan (§12) placed the complexity audit at
`test/ComplexityAudit.java`; the Build instruction listed it as
`src/ComplexityAudit.java`. It was created at `src/ComplexityAudit.java` as
instructed. This changes no behaviour, no design decision and no test — the
audit is compiled and run by the same `javac -d out src/*.java test/*.java` and
`java -ea -cp out ComplexityAudit` commands either way. It is recorded here so
the difference between the Plan and the delivered tree is visible.

### 15.3 Implementation decisions

| # | Decision | Reason |
|---|---|---|
| B-1 | `addStudent` uses `students.putIfAbsent(id, student) == null` | One hash lookup instead of two, and no window between the duplicate check and the insert (the Plan's challenge C-7, "Accepted"). Because a null `Student` is rejected before this line, a stored value is never null, so "previous value == null" unambiguously means "the ID was free". |
| B-2 | `removeStudent` uses `students.remove(id) != null` | The mirror of B-1: `remove` returns the value that was mapped, and values are never null, so null means "nothing was removed". |
| B-3 | `updateStudent` checks `containsKey` before `put` | An update must never insert (D-series contract, test A7). `putIfAbsent` is not usable here because it would *not* update an existing key. |
| B-4 | The frequency counter uses `get` then `put` per character | `getOrDefault(c, 0) + 1` would be shorter, but `get`/`put` keeps the "one map update per character" mechanism visible, which is exactly what §8.1 of the Plan predicts and what `ComplexityAudit` counts. |
| B-5 | The frequency counter iterates with `text.charAt(i)` | The counter operates on UTF-16 code units, which is what `Character` keys are. This is the behaviour the approved test F10 pins down (`"aé"` → two distinct keys). A supplementary-plane emoji would be two code units and therefore two keys — a documented consequence of the `Map<Character, Integer>` type the workbook specifies. |
| B-6 | `ProductInventory.addProduct` uses `putIfAbsent` | Same reasoning as B-1: a duplicate code must be rejected without disturbing the stored quantity (test P2). |
| B-7 | `ProductInventory.displayStock()` prints directly to `System.out` | Workbook Task 4 asks for a display, and the Part A demo needs visible output. The test asserts on the captured output rather than on a returned collection. |
| B-8 | `ComplexityAudit` counts API-level calls, never internal HashMap operations | Internal bucket probes and tree traversals are not observable from outside the JDK. The audit prints that limitation in its own header so no reader can mistake the counters for internal-operation counts. |

### 15.4 Commands executed

```
javac -d out src/*.java test/*.java
java -ea -cp out LabTestRunner
java -ea -cp out StudentDirectoryTest
java -ea -cp out CharacterFrequencyTest
java -ea -cp out ProductInventoryTest
java -ea -cp out ComplexityAudit
java -cp out Lab7Demo
run-tests.bat
```

### 15.5 Actual test results

```
> javac -d out src\*.java test\*.java
javac exit code: 0

> java -ea -cp out LabTestRunner
LabTestRunner results: 55 passed, 0 failed
---- exit code: 0 ----

> java -ea -cp out StudentDirectoryTest
StudentDirectoryTest results: 65 passed, 0 failed
---- exit code: 0 ----

> java -ea -cp out CharacterFrequencyTest
CharacterFrequencyTest results: 30 passed, 0 failed
---- exit code: 0 ----

> java -ea -cp out ProductInventoryTest
ProductInventoryTest results: 34 passed, 0 failed
---- exit code: 0 ----

> java -ea -cp out ComplexityAudit
ComplexityAudit: all self-checks passed.
---- exit code: 0 ----

> java -cp out Lab7Demo
(demonstration output - see §15.5 and README.md)
---- exit code: 0 ----
```

**Authoritative result table (actual, measured on this machine):**

| Suite | Passed | Failed | Exit code |
|---|---|---|---|
| `LabTestRunner` | **55** | **0** | 0 |
| `StudentDirectoryTest` | **65** | **0** | 0 |
| `CharacterFrequencyTest` | **30** | **0** | 0 |
| `ProductInventoryTest` | **34** | **0** | 0 |
| **Total assertions** | **184** | **0** | — |
| `ComplexityAudit` self-checks | 3 | 0 | 0 |

`run-tests.bat` (Build-stage version): **exit code 0**, with **187** `[PASS]`
lines (184 test assertions + 3 audit self-checks) and **0** `[FAIL]` lines, ending
in `All test suites passed.`

**Why 184 and not the 225 assertion call sites counted at the Test checkpoint.**
The four suites contain 225 `check(...)` call sites (§14.11.4). Forty-one of them
are the fallback `check("<behaviour> implemented", false)` lines inside the
`catch (UnsupportedOperationException)` blocks. At the Test checkpoint every
stub threw, so those 41 lines reported the 41 failures and the other 184 lines
were never reached. Now that the implementation exists, no method throws, so
those 41 fallbacks are never executed and the remaining 184 assertions run.
Correct reconciliation: 225 call sites = 182 live + 43 fallback lines; the 184 reported results = 182 live + 2 re-executions of loop call sites (corrected after the Audit, see §16, finding T23).

**No test was modified, weakened, removed or bypassed at this checkpoint.** The
four `test/*.java` files are byte-identical to their Test-checkpoint versions.

### 15.6 Complexity predictions versus measured results

> **CORRECTED RUN.** The tables in this section are from the **second** audit run,
> after the measurement methodology was corrected (see §15.6.7). The earlier
> figures — in which the "remove" row also timed the inserts that prepared the
> directory — have been **removed and replaced**, because they did not measure
> the operation their row claimed to measure.

All figures below are the output of `ComplexityAudit` on this machine, run with
the global warm-up phase. **Measured runtime is an observation on this machine,
not a complexity guarantee.** Every timed figure covers **only** the operation
named in its row; setup work is prepared before the timer starts and is never
included.

#### 15.6.1 Average case — size sweep (shape S1, distributed String IDs)

| n | operation | timed work (inside the timer) | apiCalls | elapsedNs | ns/apiCall | predicted (theory) |
|---|---|---|---|---|---|---|
| 1,000 | put | 1 directory + n addStudent | 1,000 | 117,300 | 117.30 | O(1) avg |
| 1,000 | get | n getStudent | 1,000 | 95,200 | 95.20 | O(1) avg |
| 1,000 | remove | n removeStudent | 1,000 | 106,700 | 106.70 | O(1) avg |
| 10,000 | put | 1 directory + n addStudent | 10,000 | 1,523,600 | 152.36 | O(1) avg |
| 10,000 | get | n getStudent | 10,000 | 906,900 | 90.69 | O(1) avg |
| 10,000 | remove | n removeStudent | 10,000 | 1,239,800 | 123.98 | O(1) avg |
| 100,000 | put | 1 directory + n addStudent | 100,000 | 5,105,300 | 51.05 | O(1) avg |
| 100,000 | get | n getStudent | 100,000 | 1,654,000 | 16.54 | O(1) avg |
| 100,000 | remove | n removeStudent | 100,000 | 1,226,200 | 12.26 | O(1) avg |

**Setup excluded from every row above:** the `Student` records are built once,
before any timing, and reused (`Student` is an immutable record, so sharing is
safe). The `get` row reuses one populated directory, because a get never
changes it. The `remove` row rebuilds a **fresh populated directory before every
repetition** — untimed — because the timed region empties it; the timed region
then contains the removals and nothing else.

**Reading.** Over a 100× increase in n, the per-operation cost does **not** grow:
`get` falls from 95.20 to 16.54 ns/call, `remove` from 106.70 to 12.26 ns/call,
and `put` stays between 51 and 152 ns/call. That flatness is the observable
signature of the predicted amortized **O(1)** average case. The exact per-call
figures are noisy (a single call is only microseconds), so the **scaling** is the
evidence, not any individual number.

#### 15.6.2 Worst case — shape sweep at n = 50,000

| shape | operation | timed work | apiCalls | elapsedNs | ns/apiCall | predicted (theory) |
|---|---|---|---|---|---|---|
| S1 distributed | put | n addStudent | 50,000 | 2,264,900 | 45.30 | O(1) avg / O(n) worst |
| S1 distributed | get | n getStudent | 50,000 | 1,371,700 | 27.43 | O(1) avg / O(n) worst |
| S2 shared prefix | put | n addStudent | 50,000 | 1,870,600 | 37.41 | O(1) avg / O(n) worst |
| S2 shared prefix | get | n getStudent | 50,000 | 822,000 | 16.44 | O(1) avg / O(n) worst |
| **S3 colliding** | **put** | **n addStudent** | **50,000** | **10,784,400** | **215.69** | O(1) avg / O(n) worst (treeified: O(log n)) |
| **S3 colliding** | **get** | **n getStudent** | **50,000** | **10,741,300** | **214.83** | O(1) avg / O(n) worst (treeified: O(log n)) |

**Reading — this is the most important result in the lab.** S3 uses 50,000 real
`java.lang.String` keys that all share **one** hash code (verified by the audit's
own self-checks: all distinct, all hashing to 65535). If the textbook O(n) worst
case applied, a single `get` would have to walk a chain of 50,000 entries and
would be roughly **four orders of magnitude** slower than the distributed shape.
Measured, the colliding shape is about **8× slower** per `get` (214.83 vs
27.43 ns/call) and about **5× slower** per `put` (215.69 vs 45.30 ns/call) —
which is exactly what the **prediction recorded before measuring** said would
happen: on Java 8+ a bucket holding 8 or more entries is treeified into a
red-black tree, **provided the table's capacity is at least 64**; below that
capacity the map resizes instead. A treeified bucket bounds the per-operation
cost at O(log n) instead of O(n). **The measurement is consistent with the prediction**: it rules out the O(n) chain walk, but it was taken at a single colliding size, so the O(log n) bound itself remains theory (Audit finding C3).

Honest caveat: S2 came out slightly *faster* than S1 on both operations. That is
measurement noise, not a real property of the shared prefix — the two shapes are
both ordinary hash distributions, measured in sequence within the same warmed
JVM. The only difference in the table that clearly exceeds the noise floor is
the S3 gap.

#### 15.6.3 Snapshot copying — time and auxiliary space

| n | apiCalls | entries copied | elapsedNs | ns/entry | space | predicted (theory) |
|---|---|---|---|---|---|---|
| 1,000 | 1 | 1,000 | 329,100 | 329.10 | 1,000 entries | O(n) time / O(n) space |
| 10,000 | 1 | 10,000 | 537,900 | 53.79 | 10,000 entries | O(n) time / O(n) space |
| 100,000 | 1 | 100,000 | 7,586,600 | 75.87 | 100,000 entries | O(n) time / O(n) space |

**Setup excluded:** a populated directory with n records. **Timed work:** one
`snapshot()` call, which copies all n entries. `apiCalls` = 1 (one snapshot
call); `entries` = n (the size of the copy).

**Reading.** One API call copies every entry, so the cost is linear in n in both
time and space. The total time grows from 537,900 ns at n = 10,000 to
7,586,600 ns at n = 100,000 — roughly 14× for 10× the entries, i.e. linear
within measurement noise — while the space grows exactly linearly (10,000 → 100,000
entries). The n = 1,000 row is the outlier, again a warm-up artefact.

#### 15.6.4 Character-frequency counting

| shape | length | distinctKeys | charsRead | mapGets | mapPuts | mapApiCalls | elapsedNs | ns/char | predicted (theory) |
|---|---|---|---|---|---|---|---|---|---|
| alphabet26 | 1,000 | 26 | 1,000 | 1,000 | 1,000 | 2,000 | 44,500 | 44.50 | O(n) time / O(k) space |
| alphabet26 | 100,000 | 26 | 100,000 | 100,000 | 100,000 | 200,000 | 2,912,300 | 29.12 | O(n) time / O(k) space |
| alphabet26 | 1,000,000 | 26 | 1,000,000 | 1,000,000 | 1,000,000 | 2,000,000 | 13,947,700 | 13.95 | O(n) time / O(k) space |
| one char | 1,000 | 1 | 1,000 | 1,000 | 1,000 | 2,000 | 21,500 | 21.50 | O(n) time / O(1) space |
| one char | 100,000 | 1 | 100,000 | 100,000 | 100,000 | 200,000 | 1,292,500 | 12.93 | O(n) time / O(1) space |
| one char | 1,000,000 | 1 | 1,000,000 | 1,000,000 | 1,000,000 | 2,000,000 | 9,413,600 | 9.41 | O(n) time / O(1) space |
| mixed | 1,000 | 39 | 1,000 | 1,000 | 1,000 | 2,000 | 67,200 | 67.20 | O(n) time / O(k) space |
| mixed | 100,000 | 39 | 100,000 | 100,000 | 100,000 | 200,000 | 1,100,600 | 11.01 | O(n) time / O(k) space |
| mixed | 1,000,000 | 39 | 1,000,000 | 1,000,000 | 1,000,000 | 2,000,000 | 9,908,100 | 9.91 | O(n) time / O(k) space |

**Setup excluded:** the text is built once, before any timing. **Timed work:** one
`count()` call over the whole text.

**Correcting the counter names.** The implementation performs a `get` **and** a
`put` for every character, so the total Map traffic is **2n** — one `get` and
one `put` per character. The earlier version of this table reported a single
`mapUpdates` column and described it as "one map update per character", which
was true of the `put` calls but was **not** the whole Map traffic, because the
`get` calls were not counted at all. The table now reports `mapGets`,
`mapPuts` and `mapApiCalls` separately, and the audit prints the same split in
its own header.

**Reading.** `charsRead`, `mapGets` and `mapPuts` all equal the text length
exactly, so `mapApiCalls` is exactly twice the length. `distinctKeys` stays at
26 / 1 / 39 regardless of a 1,000× change in length, which is the **O(k) space**
claim and is the clearest single piece of evidence in the lab: counting a million
characters costs the same *space* as counting a thousand. The per-character time
stays in the single-digit-to-tens-of-nanoseconds band, the linear-time signature.
The absolute `elapsedNs` grows *sub*-proportionally between the last two rows
because the per-character cost falls as the JIT specialises the loop on longer
runs; that is a compilation effect, not a complexity effect, and it is why the
audit reports the per-character figure rather than only the total.

**No collision measurement is attempted for `Character` keys.** `Character.hashCode()`
*is* the code point, so the distinct characters of any realistic alphabet have
distinct hash codes and a fully-colliding character set cannot be constructed
from real `Character` keys. The O(n²) pathological bound in the workbook's
contract is therefore **theoretical and is not claimed as measured here**.

#### 15.6.5 Predicted versus measured, summarised

| Operation | Predicted (theory) | Measured (observation) | Agreement |
|---|---|---|---|
| put / get / remove, distributed keys | O(1) average | per-call cost did not grow over a 100× range of n (get fell 95 → 17 ns) | Yes |
| put / get / remove, all-colliding keys | O(n) textbook worst; O(log n) with treeified bins | ~5–8× slower than the distributed shape, not ~50,000× slower | Consistent with treeification (single size, so O(log n) is not proven) |
| snapshot | O(n) time, O(n) space | time and entries both linear in n | Yes |
| character frequency | O(n) time, O(k) space | distinctKeys constant (observed); the call counters are derived from the code, not counted | Yes |

#### 15.6.6 The four quantities, kept separate

As required by §9.1 of the Plan, the audit reports these separately and never
combines them:

| Quantity | Value in this lab |
|---|---|
| API-level method-call counts | `apiCalls*`, `charsRead*`, `mapGets*`, `mapPuts*`, `mapApiCalls*` — **derived from the code's structure, not counted at runtime**; they equal the calls issued inside the timed region because the code issues one call per item |
| Internal HashMap operations | **not counted** — not observable from outside the JDK; the audit says so in its own header |
| Elapsed time | `elapsedNs` — wall-clock of the timed region only, includes JVM overhead |
| Auxiliary space | `entries*` (derived = n), `distinctKeys` (observed) — entry counts |

#### 15.6.7 The measurement-methodology correction

A review of the audit found that one row did not measure what its label claimed.

**The defect.** The "remove" row of the size sweep timed this whole block:

```java
StudentDirectory dir = new StudentDirectory();
for (...) dir.addStudent(id, new Student(id, "student", 90.0));   // n inserts + n allocations
for (...) dir.removeStudent(id);                                  // n removals
```

and then reported the row as **"remove"** with `apiCalls = n`. Three things were
wrong with that:

1. the **inserts and the `Student` allocations** were inside the timed region,
   so the reported time was not the cost of removal;
2. the **API-call count was wrong** — the timed region issued 2n Map calls
   (n `putIfAbsent` + n `remove`), not the n that were reported;
3. the row therefore **conflicted with the neighbouring `put` row**, which
   measured essentially the same work.

**The correction.** The `medianNanos(setup, timed)` helper already supported a
setup phase; it was simply never used. It now is:

- the `Student` records are built **once**, before any timing, and reused;
- the `remove` row uses the **setup** phase to build a **fresh populated
  directory before every repetition** (untimed) and hands it to the timed phase;
- the **timed region now contains only the n `removeStudent` calls** — no
  setup, no allocation, no construction — and `apiCalls = n` matches exactly
  what is timed.

The `put` row was corrected the same way: the `Student` construction moved out
of the timed region, leaving only the directory allocation and the n
`addStudent` calls inside it.

**The size of the correction.** The `remove` row at n = 100,000 fell from
**97.72 ns/call** (the superseded figure, which included the inserts) to
**12.26 ns/call** — the old number was dominated by the insert work, exactly
the defect described above.

**Other rows checked against the same rule:**

| Row | Timed region | Correct? |
|---|---|---|
| put | 1 directory + n `addStudent` | yes, after moving `Student` construction to setup |
| get | n `getStudent` | yes, unchanged |
| remove | n `removeStudent` | **no before the fix** — corrected |
| shape sweep put / get | n `addStudent` / n `getStudent` | yes, after the same change |
| snapshot | one `snapshot()` call | yes, unchanged |
| frequency | one `count()` call | yes, unchanged |

**Everything else in §15.6 was re-measured, not patched.** The corrected audit
was re-run from scratch, `out/` was deleted first so that no stale class file
could be used, and all the tables above are the output of that run. The
superseded numbers were deleted rather than left in place next to the new ones,
because two sets of figures for the same row would make the evidence
unreadable and would invite the reader to pick whichever looked better.


### 15.7 Problems encountered and how they were addressed

**P-1 — A measurement artefact, found by looking at the first results.**
The first run of `ComplexityAudit` produced a size sweep in which the n = 1,000
row was *slower per call* than the n = 100,000 row (272 ns vs 108 ns for `put`),
and in which the frequency counter's per-character cost fell by 10× between the
smallest and largest input. The cause was the one documented in Lab 3's
REFLECTION.md: whichever measurement runs first pays for the JVM's on-demand
compilation. *Fix:* a `warmUpJit()` phase now runs the whole workload once at
the largest size — 100,000 distributed keys, a snapshot, a million-character
count and 50,000 colliding keys — and discards the result, so every section is
measured in the same warmed state. The audit prints `Warming up the JVM
(discarded, not reported)...` so a reader can see that it happened. The numbers
reported in §15.6 are from the **second** (post-fix) run.

**P-2 — The colliding shape could not be built at n = 100,000.**
The construction that produces real equal-hash `String` keys (fixing the hash
target and solving for the third character) yields about 73,000 distinct
three-character strings sharing one hash code, which is fewer than 100,000.
*Fix:* the shape sweep uses **n = 50,000 for all three shapes**, so the
comparison stays like-for-like, and the size sweep still covers n = 100,000 for
the distributed shape. This is stated in the audit's own section 2 header.

**P-3 — The demo printed the space character as an unlabelled row.**
`count("the quick brown fox")` includes the space, and printing the raw
character produced a row reading `    = 3`. *Fix:* the demo now prints the key
in quotes (`' ' = 3`), which makes every row readable without changing the
counter's behaviour.

**P-4 — The plan and the instruction disagreed about where the audit lives.**
The approved Plan put `ComplexityAudit.java` in `test/`; the Build instruction
listed `src/`. *Fix:* the instruction was followed and the file created at
`src/ComplexityAudit.java`; the deviation is recorded in §15.2. No behaviour,
decision or test changed.

**P-5 — The `run-tests.bat` truncation mistake during verification.**
An early verification command piped the script output through `Select-Object
-First 30`, which terminated the pipeline early and made the script appear to
exit with code -1. *Fix:* the script was re-run writing to a temporary file and
reading the file afterwards; the true exit code is **0**. The temporary file was
deleted afterwards, so it is not part of the submitted project.

**P-6 — The "remove" benchmark did not measure removal.**
Found by a review of the measurement methodology after the Build checkpoint. The
timed region included the directory allocation, n `Student` allocations and n
`addStudent` calls, while the row was labelled "remove" and reported
`apiCalls = n` — the timed region actually issued 2n Map calls. *Fix:* the
`setup` phase of `medianNanos(setup, timed)` now builds a fresh populated
directory before every repetition (untimed) and hands it to the timed phase,
which contains nothing but the n `removeStudent` calls. The `put` row received
the same treatment. The full account, including the size of the correction, is
in §15.6.7.

**P-7 — A stale class file produced misleading output during the correction.**
While rewriting the audit, `warmUpJit()` was dropped by accident, so
`javac` failed with `cannot find symbol: method warmUpJit()`. The audit was
then run anyway and printed **the previous build's output** — the old column
headers, the old `mapUpdates` counter — from the class file left in `out/`.
Those numbers were not the new code's numbers. *Fix:* `warmUpJit()` was
restored, and the rebuild now **deletes `out/` before compiling** so that a
stale class file can never be mistaken for a fresh result. The tables in §15.6
come from that clean rebuild.

**P-8 — The frequency counter's Map traffic was under-reported.**
The audit reported a single `mapUpdates` column and described it as "one map
update per character", which counted the `put` calls but not the `get` calls the
counter also performs. *Fix:* the audit now reports `mapGets`, `mapPuts` and
`mapApiCalls` (= 2 × length) separately, and states in its own header that
`mapPuts` alone is not the whole Map traffic. The implementation itself was not
changed — it has always performed one `get` and one `put` per character; only
the reporting was wrong. *Audit correction (§16, C15):* the counters are still derived from the code, not instrumented; the fix corrected how the traffic was described, not how it is counted. The audit now marks derived columns with `*`.

### 15.8 Defects and limitations remaining after the Build checkpoint

1. **No supplied acceptance runner exists.** `test/LabTestRunner.java` is
   agent-authored (disclosed in the file header, in the run output, and in
   README.md). The instructor's starter was never placed in the workspace.
2. **The audit's absolute timings are machine-specific.** They are valid as
   *relative* evidence on this machine and nothing more. Any claim of the form
   "a `get` takes X ns" would be false on other hardware; the log therefore
   makes only scaling claims.
3. **Small-n rows remain warm-up sensitive.** The n = 1,000 rows are still the
   least reliable numbers in the audit, even after the global warm-up, because
   1,000 operations take only tens of microseconds.
4. **The frequency counter counts UTF-16 code units, not code points.** A
   character outside the Basic Multilingual Plane occupies two `char` values and
   is therefore counted as two keys. This follows from the workbook's
   `Map<Character, Integer>` type and is documented in the class Javadoc and in
   decision B-5.
5. **`StudentDirectory` is not thread-safe.** Neither is a raw `HashMap`, and
   the workbook's scope does not include concurrency. `ConcurrentHashMap` would
   be the fix, and it is out of scope.
6. **The O(n²) pathological bound for frequency counting is untested by
   necessity**, not by omission — see §15.6.4.
7. **The audit has not been audited.** The Audit checkpoint has not started.

### 15.9 Build checkpoint status

| Checkpoint | Status |
|---|---|
| Plan | Complete (§1–§13) |
| Test | Complete (§14), including two review-driven correction rounds |
| **Build** | **Complete, including a measurement-methodology correction.** 3 stub classes implemented, 3 new files created, scripts updated, compile clean, **184 assertions passed / 0 failed**, 3 audit self-checks passed, `run-tests.bat` exit code 0, `javac --release 17` exit 0. The complexity audit was corrected after review (P-6, P-7, P-8) and **re-measured from a clean build**; §15.6 now carries only the corrected figures. |
| Audit | **Complete — see §16.** |
| Git | Pushed to GitHub by the student. |


---

## 16. AUDIT checkpoint (executed)

**Date:** 2026-11-15. **Agents:** `@test-reviewer` and `@complexity-auditor` (read-only, defined in `.opencode/agents/`). No files were changed by the agents.

**Result:** 46 findings (T1–T25 tests, C1–C21 complexity): 2 high, 16 medium, 28 low. I reviewed every finding and checked the ones marked (V) against the source myself. Decisions below.

### 16.1 Highest-severity findings

- **T13** (ProductInventoryTest:173-176): displayStock uses contains("5")/contains("9"); does not tie quantity to code. **Accepted.** Fix now. A swapped output would still pass. Verification: Read test lines 173-176 (V).
- **C13** (README:197): charsRead/mapGets/mapPuts/mapApiCalls are literals from length, shown as 'measured'. **Accepted.** Fix now by relabelling as derived from the implementation. I flagged this myself. Verification: ComplexityAudit:352-354 prints length, length*2 (V).

### 16.2 Rejected recommendations, with reasons

- **T14** (LabTestRunner:90): 'did not store second record' check is redundant. **Rejected.** Redundant but harmless; the two preceding assertions are the real checks. Left as is.
- **T16** (LabTestRunner:92): Per-section catch collapses every exception into one failure. **Rejected.** Overstated. The catch only handles UnsupportedOperationException (Test-stage stubs). Any other exception propagates and fails loudly, so nothing is hidden.
- **T12** (ProductInventoryTest:26): Removal matrix item uncovered for ProductInventory (no removal API). **Rejected.** Overreach. Workbook Task 4 asks only add, update and display. The Removal matrix row belongs to StudentDirectory, which is tested. No removal API is required.
- **T20** (LabTestRunner:243): equals/hashCode on a record tests the JDK. **Rejected.** Kept on purpose: documents the immutable value-type contract the directory relies on.
- **C11** (ComplexityAudit:346-350): Discarded result could be optimised away. **Rejected.** Speculative. count() builds and fills a HashMap; I have no evidence the JIT eliminates it, and the numbers scale linearly. Not changed.
- **C12** (ComplexityAudit:181-196): Setup GC could inflate remove timing. **Rejected.** System.gc() is a hint and adds its own noise. Median of 5 runs after warm-up already damps this. Documented as a limitation instead.

### 16.3 Accepted recommendations

| ID | Severity | Finding | Reason / action |
|---|---|---|---|
| T13 | high | displayStock uses contains("5")/contains("9"); does not tie quantity to code | Fix now. A swapped output would still pass. |
| T1 | medium | updateStudent(id, null) untested | Fix now. Real gap in invalid-operation coverage. |
| T2 | medium | updateStudent with mismatched record ID untested | Fix now. Update path has the same D8 rule as add. |
| T4 | medium | Removing the last record (size 0) never tested | Fix now. Boundary case. |
| T7 | medium | adjustStock on unknown code untested | Fix now. Missing-Key case incomplete for inventory. |
| T8 | medium | Exact-zero boundary of negative-stock guard untested | Fix now. Classic off-by-one boundary (updated < 0). |
| T15 | medium | containsStudent 'agrees' compares two implementation values | Fix now. A containsStudent that ignored the key could still pass the agreement check. |
| T22 | medium | Implemented displayStock test weaker than the contract in AGENT_LOG | Same defect as T13; fixed together. |
| T3 | low | Null-name rule only tested on add | Document only for now; low value. |
| T5 | low | Empty-string key and case-differing keys untested | Document only for now; low value. |
| T6 | low | Student with null id() never passed | Fix now. Cheap, covers a branch in isValidRecord. |
| T9 | low | Quantity 0 never asserted legal | Document only for now. |
| T10 | low | displayStock on empty inventory untested | Document only for now. |
| T11 | low | Tab/newline and surrogate pair untested | Document only; matches my stated UTF-16 limitation. |
| T17 | low | Sum check derives total from the map under test | Document only; the other assertions pin exact counts. |
| T18 | low | Two grades checked in one condition | Document only. |
| T19 | low | 'snapshot is mutable' check inside if (snap != null) | Document only; the preceding check already fails on null. |
| T21 | low | freq.get('a') == 5 unboxes null -> NPE | Fix now if time allows. A missing key aborts the suite instead of a clean [FAIL]. |
| T23 | low | '184 + 41 = 225' mis-stated | Fix now. Agent is right. |
| T24 | low | §7.2 matrix rows disagree with the tests | Fix now in the log. |
| T25 | low | Header still says stubs/expected to FAIL | Fix now. Stale after Build. |
| C13 | high | charsRead/mapGets/mapPuts/mapApiCalls are literals from length, shown as 'measured' | Fix now by relabelling as derived from the implementation. I flagged this myself. |
| C1 | medium | HashMap-vs-list comparison cites measurements never taken | Fix now. Audit never times an ArrayList/LinkedList. |
| C3 | medium | Treeification 'confirmed' from one colliding size | Fix now. One ratio cannot prove O(log n). |
| C4 | medium | §15.6.1 not verbatim (columns differ) | Fix now: say the prediction column is abbreviated. |
| C9 | medium | Shape-sweep put label says 'n addStudent' but timer includes a new directory | Fix now. Label should match the timed region. |
| C14 | medium | apiCalls/entries are literals; only elapsedNs and distinctKeys observed | Fix now with C13. |
| C15 | medium | P-8 'fix' only corrected reporting, not counting | Fix now in the log. |
| C17 | medium | entries filed under call counts; spaceEntries never printed | Fix now in the log. |
| C16 | low | Snapshot timed region also calls copy.size() | Fix now. Tiny, but the label says one call. |
| C2 | low | 'same band / flat' misdescribes 95 -> 17 ns/call | Fix now. It fell; it did not stay flat. |
| C5 | low | 'about 5x slower' but get was ~8x | Fix now. |
| C6 | low | Snapshot 'noise' dismisses a rising per-entry trend | Document only: reword as 'rising with n'. |
| C7 | low | 'Prediction recorded before measuring' not evidenced in the artifact | Fix now: point to §9.3. |
| C8 | low | S2 'clustered' is wrong; String hash spans whole string | Fix now. Correct Java behaviour. |
| C10 | low | Put row includes one directory allocation | Document only. Already disclosed in code comment. |
| C18 | low | Space column mixes states | Document only. |
| C19 | low | elapsedNanos vs elapsedNs spelling | Fix now. Trivial. |
| C20 | low | Put is amortized O(1), not plain O(1) | Fix now. Correct. |
| C21 | low | Plan says O(n log k) vs README O(n) | Document only. |

### 16.4 Independently evaluated agent limitation

The agents were not perfect. `@test-reviewer` claimed (T12) that the Removal case is uncovered for `ProductInventory`. I checked the workbook: Task 4 requires only add, update and display, and the Removal row of the acceptance matrix is about the student directory, which is tested. I rejected it. It also claimed (T16) that the per-section `catch` collapses every exception into one failure. I read the code: it catches only `UnsupportedOperationException`, so any other exception propagates and fails loudly. `@complexity-auditor` suggested `System.gc()` and result-consumption changes (C11, C12) without evidence they matter, so I rejected those too. Its counter findings (C13–C15) were correct and matched what I already knew about the audit.

### 16.5 Fixes applied after the audit

All findings marked "Fix now" were applied on 2026-11-15, then the project was rebuilt from a clean `out/` and every suite re-run.

- **Tests (strengthened only, none weakened or removed):** T13/T22 (displayStock now asserts the exact `P1 -> 5` / `P2 -> 9` lines and one line per entry), T15 (absolute `containsStudent` expectations added; the agreement checks were kept), T1, T2, T4, T6 (new cases A16–A19 in `StudentDirectoryTest`), T7, T8 (new cases P11–P12 in `ProductInventoryTest`), T21 (`countOf` helper so a missing key is a failed check, not an NPE), T25 (stale stub header in `LabTestRunner` replaced). The earlier statement in §15 that the four test files were byte-identical to the Test checkpoint is therefore true only for the Build checkpoint; after the Audit, three of them were extended.
- **Audit code (`ComplexityAudit`):** C13/C14 (derived columns are now marked with `*` and the header states that only `elapsedNs` and `distinctKeys` are observed), C9 (shape-sweep put label now says "1 directory + n addStudent"), C16 (the snapshot correctness check moved outside the timer; a volatile sink keeps the copy alive), C8 (S2 relabelled as not hash-clustered), C19 (one spelling, `elapsedNs`).
- **Docs:** C1, C2, C3, C5, C7, C20 and C4/C15/C17 wording in `README.md` and §15.6/§15.7; T23 (225 = 182 live + 43 fallback), T24 (§7.2 rows A3, A4, F8 now match the code).
- **Result after fixes:** `LabTestRunner` 55, `StudentDirectoryTest` 82, `CharacterFrequencyTest` 30, `ProductInventoryTest` 40 = **207 assertions, 0 failures**; `ComplexityAudit` self-checks pass; `javac --release 17` clean.
- **Accepted but only documented, not changed (low value):** T3, T5, T9, T10, T11, T17, T18, T19, C6, C10, C18, C21.
- **Rejected:** T12, T14, T16, T20, C11, C12 (reasons in §16.2).
