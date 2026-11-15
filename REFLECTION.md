# REFLECTION — Lab 7: HashMap and Key–Value Structures

Name: Lance Xyzherlee A. Bimbo

> DRAFT built from AGENT_LOG.md. Items marked [CONFIRM] are things only you can
> verify. Rewrite anything in your own words before submitting.

## Technical reflection

**1. Which invariant or edge case caused the most difficult failure? Cite the failing test.**
The invariant "the record's own ID must equal its map key" (decision D8) was the
subtlest rule. Test A11 (`StudentDirectoryTest`, `addStudent("A", new Student("B", ...))`
must return false and store nothing) fails against the stub and would also fail
against a naive `putIfAbsent`-only implementation, because the map alone cannot
notice that the key and the record disagree. At the Test checkpoint, F9
(`count(null)` must throw `IllegalArgumentException`) failed with
"actually threw: UnsupportedOperationException", which showed the exact gap.
[CONFIRM: replace with the failure that was hardest for YOU.]

**2. How did measured operations compare with your complexity prediction?**
They agreed. With distributed String IDs, per-call cost of get/remove stayed flat
over a 100x range of n (get 95 -> 17 ns/call, remove 107 -> 12 ns/call), matching
O(1) average. Snapshot time and entries grew linearly with n (O(n) time and
space). For frequency counting, charsRead = mapGets = mapPuts = length, so time is
O(n) and space is O(k) distinct keys. The one surprise was the worst case:
50,000 keys sharing one hash code were only ~5-8x slower than distributed keys,
not ~50,000x. That is because Java 8+ HashMap turns a bucket of 8+ entries into a
red-black tree (when table capacity is >= 64), giving O(log n) per operation. I
predicted this before measuring. The O(n^2) bound for counting could not be
measured, because Character.hashCode() is the code point, so distinct characters
never collide. The apiCalls counters are derived from the code, not instrumented.

**3. Which agent recommendation did you verify independently, and what evidence determined your decision?**
[WRITE AFTER YOUR AUDIT. Name one @test-reviewer or @complexity-auditor
recommendation, how you checked it (re-run, read the JDK behaviour, a test of your
own), and whether you accepted or rejected it. Do not invent this.]
Already-real candidate: review of the "remove" benchmark (log P-6) showed it also
timed n inserts and reported apiCalls = n while issuing 2n calls. I confirmed it by
reading the timed block, fixed it with the setup phase, and re-measured:
remove at n = 100,000 fell from 97.72 to 12.26 ns/call.

**4. What implementation choice would you change for a much larger dataset?**
snapshot() copies every entry (O(n) time and memory) on every call. For millions
of records I would return an unmodifiable view, or copy-on-write, or let callers
iterate instead of copying. I would also give the HashMap an initial capacity to
avoid repeated resizing, and use ConcurrentHashMap if threads were involved.

**5. Identify one limitation that remains in your submitted solution.**
The counter counts UTF-16 chars, not Unicode code points, so a character outside
the Basic Multilingual Plane (e.g. an emoji) is counted as two keys. It follows
from the workbook's `Map<Character,Integer>` type. Also: StudentDirectory is not
thread-safe, and LabTestRunner is agent-authored because the instructor starter
was not in the workspace.

## Reflection guide

- **Why is HashMap more efficient than ArrayList/LinkedList?** Lists search by
  scanning, O(n). HashMap hashes the key to a bucket and goes straight there, O(1) average.
- **What problems suit key–value structures?** Lookups by unique identifier: student
  ID -> record, product code -> stock, candidate -> votes, query -> cached result.
- **Collision issues and how to avoid them?** Different keys with the same hash share
  a bucket and are chained; equals() keeps results correct but speed drops toward O(n)
  (O(log n) once treeified). Avoid with keys that have well-spread, stable hashCode()
  (String, records, not mutable objects).

## Challenge question
When two different keys have the same hash code, HashMap stores both in the same
bucket as a linked chain (a red-black tree once the bucket reaches 8 entries and the
table has >= 64 slots). get() hashes to the bucket, then compares keys with equals()
along the chain.

## Submission checklist
- [ ] src/ has the completed implementation, no TODOs
- [ ] test/ has normal, boundary and invalid-operation tests
- [ ] All tests pass with assertions enabled (184 assertions)
- [ ] AGENT_LOG.md records the plan before Build edits
- [ ] Accepted/rejected agent suggestions have my reasons (Audit section 16)
- [ ] Predictions compared with measured evidence (AGENT_LOG 15.6)
- [ ] No out/, credentials or personal data submitted
- [ ] I can explain and modify the implementation in a live check
