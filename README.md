This is a program for solving the clustering problem of the Colorado School of Mines Fall 2016 AlgoBowl competition.

It takes in a set of points in the form of: <br />
10        // n number of points <br />
3         // k number of allowable sets <br />
3 6 9     // x y z of first point <br />
-5 14 55  // x y z of second point <br />
...

All numbers in the input file must be whole numbers. The number of points must be 1000 or less, the number of allowable sets must be between 2 and 20, and each of the coordinates must have x y z coordinates between -1000 and 1000. The program should work outside of these bounds, but the functionality might be reduced.

The program returns the n points clustered into k sets in which the maximum Manhattan distance between any two points in any set is minimized.

The output in filenameOUTPUTS.txt is given in the form: <br />
8       // maximum distance between any two points in the same set <br />
1 3 5   // points included in set 1 <br />
2 4 6   // points included in set 2 <br />
...