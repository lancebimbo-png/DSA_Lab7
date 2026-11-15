REFLECTION — Lab 7: HashMap and Key–Value Structures

Name: Lance Xyzherlee A. Bimbo

Technical reflection

1. Which invariant or edge case caused the most difficult failure? Cite the failing test.

The hardest invariant was "the record's own ID must equal the key it is stored under" (decision D8). A HashMap cannot enforce that by itself: it will happily store Student("B", ...) under key "A", and then the key and the record disagree. Test A11 (addStudent("A", new Student("B", "Bob", 91.0)) must return false and store nothing) failed against the stub at the Test checkpoint and forced me to add an explicit check, which ended up in a shared isValidRecord() helper. The audit then showed that the same rule on the update path had no test at all, so I added A17 (updateStudent("A", new Student("B", ...)) must return false and leave Alice unchanged). The lesson was that a rule enforced in two methods needs a test in both. Another failure I saw clearly was F9: count(null) threw UnsupportedOperationException where the test expected IllegalArgumentException, and the runner printed the exact type it got, which made the gap easy to close.

2. How did measured operations compare with your complexity prediction?

They agreed, with one caveat. With distributed String IDs the per-call cost of get and remove did not grow over a 100× range of n (get fell from 95 to 17 ns/call, remove from 107 to 12 ns/call), which matches O(1) average. The fall itself is a JIT and cache effect, not a complexity effect. Snapshot time and entry count grew linearly with n, matching O(n) time and space. For the frequency counter, distinctKeys stayed constant for a given alphabet. The surprise was the worst case: 50,000 String keys sharing one hash code were only about 5× slower for put and 8× slower for get, not thousands of times slower, because Java 8+ turns a bucket of 8 or more entries into a red-black tree when the table has at least 64 slots. I predicted that in the Plan before measuring. The measurement is consistent with treeification but was taken at one size, so I can't claim O(log n) as measured. The O(n²) pathological bound for counting could not be tested, because Character.hashCode() is the code point and distinct characters never collide. The call counters in the audit (apiCalls*, mapGets* and so on) are derived from the code, not counted at runtime, and are now marked with *.

3. Which agent recommendation did you verify independently, and what evidence determined your decision?

@test-reviewer finding T12 said the Removal case was uncovered for ProductInventory. I checked the workbook: Task 4 only asks for add, update and display, and the Removal row of the acceptance matrix belongs to StudentDirectory, which is tested. I rejected it. I also rejected T16 after reading the code: the catch blocks only handle UnsupportedOperationException, so any other exception still fails loudly. I accepted T13 after reading the test: output.contains("5") would still pass if the quantities were swapped, so I replaced it with exact P1 -> 5 and P2 -> 9 line checks. @complexity-auditor finding C13 was also correct: the audit's call counters were literals derived from the length, so I relabelled them as derived. Checking each claim against the source, instead of accepting all 46, is what let me reject the six that were wrong or speculative.

4. What implementation choice would you change for a much larger dataset?

snapshot() copies every entry on every call, which is O(n) time and memory. For millions of records I would return an unmodifiable view, use copy-on-write, or let callers iterate instead of copying. I would also give the HashMap an initial capacity to avoid repeated resizing, and switch to ConcurrentHashMap if several threads needed access.

5. Identify one limitation that remains in your submitted solution.

The frequency counter counts UTF-16 char values, not Unicode code points, so a character outside the Basic Multilingual Plane (an emoji, for example) is counted as two keys. This follows from the workbook's Map<Character, Integer> type. Also, StudentDirectory is not thread-safe, and LabTestRunner is agent-authored because the instructor's starter was not in my workspace (disclosed in the README and the runner header).

Reflection guide
How is a HashMap more efficient than an ArrayList or LinkedList? A list finds an item by scanning, which is O(n). A HashMap hashes the key to a bucket and goes straight there, which is O(1) on average. My audit measured only the HashMap side, so the list comparison is theory.
What types of problems are best solved with key–value structures? Lookups by a unique identifier: student ID to record, product code to stock, candidate to votes, query to cached result.
What are the potential issues of hash collisions and how can they be avoided? Different keys with the same hash share a bucket and are chained. equals() keeps results correct, but speed drops toward O(n), or O(log n) once the bucket is treeified. Avoid this with keys that have a well-spread, stable hashCode(), such as Strings or records, not mutable objects.
Challenge question

If two different keys have the same hash code, HashMap stores both in the same bucket as a linked chain. Since Java 8 the chain becomes a red-black tree once the bucket reaches 8 entries and the table has at least 64 slots. get() hashes the key to the bucket, then compares keys with equals() along the chain until it finds the match.