
!follow(threat).

/* Plans */

+!follow(threat) : aircraft(DIR, DIST)
   <- turn(DIR);
      !follow(threat).

+!follow(threat) : true
   <- turn(DIR);
      !follow(threat).


+!follow(threat).



