# Lab 7 — HashMap and Key–Value Structures

CpE 211 · Data Structures and Algorithms · Computer/Electronics Engineering
Instructor: Engr. Jamie Eduardo Rosal, MSCpE
Workbook: **DSA-WB-07** — `Lab_07_HashMap_KeyValue_Workbook.docx`

> **Note on the starter kit.** The instructor starter for this lab (the original
> `src/StudentDirectory.java` and `test/LabTestRunner.java`) was **never placed in
> this workspace**, and no starter repository exists for it. The project was
> therefore scaffolded from the written contract in the workbook after explicit
> approval, and **`test/LabTestRunner.java` is agent-authored, not
> instructor-supplied.** Every other test file is student-authored. This is
> disclosed here, in the runner's own header, and in `AGENT_LOG.md`.

## Directory structure

```
Lab7
├── src/
│   ├── Student.java                    # immutable record: id, name, grade
│   ├── StudentDirectory.java           # Part B: keyed student directory (HashMap-backed)
│   ├── CharacterFrequencyCounter.java  # Part B: character-frequency map
│   ├── ProductInventory.java           # Part A Task 4: product code -> quantity
│   ├── HashMapBasics.java              # Part A Tasks 1-3: plain HashMap fundamentals
│   ├── Lab7Demo.java                   # runnable demonstration (main)
│   └── ComplexityAudit.java            # complexity measurement harness
├── test/
│   ├── LabTestRunner.java              # acceptance suite (agent-authored) - 55 assertions
│   ├── StudentDirectoryTest.java       # student-authored edge cases - 65 assertions
│   ├── CharacterFrequencyTest.java     # student-authored edge cases - 30 assertions
│   └── ProductInventoryTest.java       # student-authored Part A cases - 34 assertions
├── out/                                # generated .class files (git-ignored)
├── AGENT_LOG.md                        # plan, test, build and audit checkpoints
├── README.md
├── .gitignore                          # out/, *.class, .vscode/
├── .gitattributes                      # line-ending rules (*.sh stays LF)
├── run-tests.bat                       # Windows: compile + run everything
└── run-tests.sh                        # macOS/Linux equivalent
```

## Compile and run

```bash
javac -d out src/*.java test/*.java
java -ea -cp out LabTestRunner
```

Or run everything at once:

| Platform | Command |
|---|---|
| Windows | `run-tests.bat` |
| macOS / Linux | `bash run-tests.sh` |

The script compiles everything, runs the four test suites with assertions
enabled, runs the complexity audit, and prints the Part A / Part B demonstration.
It stops at the first failure and reports which suite failed.

Individual suites:

```bash
java -ea -cp out LabTestRunner            # 55 assertions
java -ea -cp out StudentDirectoryTest     # 65 assertions
java -ea -cp out CharacterFrequencyTest   # 30 assertions
java -ea -cp out ProductInventoryTest     # 34 assertions
java -ea -cp out ComplexityAudit          # complexity measurements + self-checks
java -cp out Lab7Demo                     # the demonstration output
```

**Expected result (verified 2026-11-15):** 184 assertions, **0 failures**, every
suite exiting 0, and `run-tests.bat` exiting 0.

## API

### `StudentDirectory` (Part B)

| Method | Returns | Behaviour |
|---|---|---|
| `addStudent(String id, Student student)` | `boolean` | `false` if the ID is null, the record is null, the record's own ID differs from the key, or the ID is **already taken** — the existing student is kept, never overwritten |
| `getStudent(String id)` | `Student` | the stored record, or `null` when the ID is unknown or null |
| `updateStudent(String id, Student student)` | `boolean` | replaces an existing record; `false` for an unknown ID — an update never inserts |
| `removeStudent(String id)` | `boolean` | `true` if a record was removed, `false` otherwise |
| `snapshot()` | `Map<String, Student>` | a **defensive copy**; the caller may modify it freely without touching the directory |
| `size()` | `int` | number of records |
| `containsStudent(String id)` | `boolean` | whether the ID is present |

### `CharacterFrequencyCounter` (Part B)

| Method | Returns | Behaviour |
|---|---|---|
| `count(String text)` | `Map<Character, Integer>` | every character counted **exactly as supplied**: uppercase and lowercase are separate keys, spaces and punctuation are counted. Empty text → empty map. `null` → `IllegalArgumentException` |

### `ProductInventory` (Part A Task 4)

| Method | Returns | Behaviour |
|---|---|---|
| `addProduct(String code, int quantity)` | `boolean` | `false` for a null code, a negative quantity, or a duplicate code |
| `updateStock(String code, int quantity)` | `boolean` | sets an **absolute** quantity; `false` for an unknown code |
| `adjustStock(String code, int delta)` | `boolean` | applies a signed delta (borrow / return); `false` if it would make the stock negative |
| `getQuantity(String code)` | `int` | the quantity, or `0` for an unknown code |
| `displayStock()` | `void` | prints `code -> quantity` for every entry, via for-each over `entrySet()` |
| `size()` | `int` | number of products |

## Required explanations

### Why is a `HashMap` more efficient than an `ArrayList` or a `LinkedList`?

Both `ArrayList` and `LinkedList` are **lists**, and finding something in a list
always means walking through it one element at a time. If the item you want is
in the middle, you look at about half the list; if it is not there at all, you
look at all of it. So for **both** structures the average search is **O(n)** — the
list grows, and so does the search. A `LinkedList` does not rescue you either:
its nodes are scattered in memory, so walking one is slower than reading an
array, and the number of steps is the same.

A `HashMap` works completely differently. It computes a hash code from the key,
uses that number to work out which bucket must hold the entry, and goes
**straight to that bucket**. Nothing else is visited. That is an average
**O(1)** lookup — "constant time" — regardless of how many records are stored.

The measured evidence from `ComplexityAudit` agrees: over a 100× increase in
student count, the per-operation cost of `get` stayed in the same band
(95 ns → 17 ns per call) instead of growing with n, which is the signature of
constant-time access.

The trade-off is that a `HashMap` does **not** keep its entries in insertion
order, has more per-entry overhead than a list, and its worst case degrades when
many different keys happen to share the same bucket.

### What types of problem are best solved with a key–value structure?

Any problem phrased as "look something up by its identifier": a student
directory keyed by student ID, an inventory keyed by product code, a vote count
keyed by candidate, a cache keyed by query, a name-to-ID mapping. The common
shape is: a unique, stable key identifies a value, and the value is read far
more often than the collection is scanned. That is exactly the case where
turning an O(n) scan into an O(1) lookup pays for itself.

### What are the potential issues with hash collisions, and how can they be avoided?

A **hash collision** happens when two different keys produce the same hash code,
so both must live in the same bucket. The workbook's Challenge Question asks how
`HashMap` handles this: the first entry goes into the bucket, and a second entry
with the same hash is chained onto that bucket. A lookup then compares the key of
every entry in that bucket using `equals()`, so correctness is never
jeopardised — only speed.

A collision chain is what makes the textbook worst case **O(n)**: with every key
in one bucket, each operation walks the whole chain.

Modern Java reduces that risk. Since Java 8, a bucket that holds **8 or more**
entries is turned into a balanced tree (a *red-black tree*) instead of a plain
chain — but **only if the table itself is big enough**, that is, only when the
map's internal table has a capacity of at least 64 slots. If the table is
smaller than that, the map makes room by growing the table instead of
treeifying. When the bucket does become a tree, each lookup inside it costs
O(log n) rather than O(n), so the damage a collision storm can do is much
smaller than the textbook bound suggests.

A collision chain is therefore best avoided in the first place, by choosing keys
whose `hashCode()` spreads well — for example `String` keys, or the `record`
keys used here, rather than a mutable object whose hash code changes over time.
For the `Character` keys of the frequency counter, `hashCode()` *is* the code
point, so distinct characters of a realistic alphabet never collide, which is
why the O(n²) pathological bound for counting is theoretical here and could not
be measured.

The `ComplexityAudit` measures this directly with 50,000 **real** `String` keys
that all share one hash code. The colliding shape turned out to be only about
**5× slower** per operation than the distributed shape — not the four orders of
magnitude the textbook O(n) bound would suggest — because the bucket is
treeified.

### Workbook conflict: replace versus reject

The workbook's Part A says a `HashMap` **replaces** the value when the same key
is put twice, while Part B requires duplicate student IDs to be **rejected**.
Both behaviours are demonstrated, in different places:

- `HashMapBasics` uses a raw `HashMap` and shows the **replacement** behaviour.
- `StudentDirectory` implements the stricter Part B rule and **rejects** a
  duplicate ID, keeping the original record.

### Legacy heading

The workbook's older section is titled "Lab 8: HashMap and Key-Value Data
Structures" while Part B and the workbook ID say Lab 7. This project is
submitted as **Lab 7 — HashMap and Key–Value Structures**, workbook ID
**DSA-WB-07**.

## Complexity summary

| Operation | Average | Worst | Measured observation |
|---|---|---|---|
| `put` / `get` / `remove` | O(1) | O(n) textbook; O(log n) with treeified bins | flat per-call cost over a 100× range of n |
| `snapshot()` | O(n) time, O(n) space | O(n) | time and entries both linear in n |
| character frequency | O(n) time, O(k) space | O(n²) textbook (not exercisable with `Character` keys) | `charsRead` = length; `mapGets` = `mapPuts` = length, so `mapApiCalls` = 2 × length; `distinctKeys` constant |

Each timed figure covers **only** the operation named in its row — the setup is
prepared before the timer starts and is never included. The audit prints that
split in its own output.

Full measurements, the prediction that was recorded before measuring, and the
measured results are in `AGENT_LOG.md` §15.6. Measured runtime is an observation
on one machine — it is never presented as a complexity guarantee.

## Submission checklist

- [x] `src/` contains the completed implementation, with no TODOs remaining.
- [x] `test/` contains normal, boundary and invalid-operation tests.
- [x] All tests compile and pass with assertions enabled (184 assertions, 0 failures).
- [x] `AGENT_LOG.md` records the student plan before Build edits were allowed.
- [x] `out/`, credentials and generated files are excluded by `.gitignore`.
- [x] Git repository, commit and push — **done**: `Lab7` on GitHub, branch `main`,
      one commit, `out/` and `node_modules/` excluded.
- [ ] Audit checkpoint — **not started**.
