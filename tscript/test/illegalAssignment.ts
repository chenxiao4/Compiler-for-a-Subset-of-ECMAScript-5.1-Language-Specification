//
// this is the single semantic error detected by the system
// prior to evaluating the program, the system is responsible
// for detecting that the left-hand-side of an assignment is
// illegal
//

var abc;

print abc = 1;
print 1 = abc;

