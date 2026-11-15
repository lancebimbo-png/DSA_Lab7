#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"
rm -rf out
javac -d out src/*.java test/*.java
for t in LabTestRunner StudentDirectoryTest CharacterFrequencyTest ProductInventoryTest ComplexityAudit; do
  java -ea -cp out $t
done
java -cp out Lab7Demo
echo "All test suites passed."
