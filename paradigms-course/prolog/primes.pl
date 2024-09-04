concat([], B, B).
concat([H | T], B, [H | R]) :- concat(T, B, R).

range(N, L, []) :- N = L.
range(N, L, [N | T]) :- N < L, N1 is N + 1, range(N1, L, T).

check(N) :-
  N > 1,
  \+ check_prime(N, 2).
check_prime(N, X) :-
  X =< sqrt(N),
  (0 is mod(N, X);
  check_prime(N, X + 1)).

assert_primes([]).
assert_primes([H | T]) :-
  \+ check(H),
  assert_primes(T).
assert_primes([H | T]) :-
  check(H),
  assert(primes_table(H)),
  assert_primes(T).

init(MAX_N) :-
  range(1, MAX_N, S),
  assert_primes(S).

prime(N) :- primes_table(N).
composite(N) :- \+ primes_table(N).

next_prime(N, R) :-
  N1 is N + 1,
  prime(N1),
  R = N1.
next_prime(N, R) :-
  N1 is N + 1,
  \+ prime(N1),
  next_prime(N1, R).

decompose(N, P, S, R) :-
  N < P,
  R = S.
decompose(N, P, S, R) :-
  N >= P,
  \+ 0 is mod(N, P),
  next_prime(P, P1),
  decompose(N, P1, S, R).
decompose(N, P, S, R) :-
  N >= P,
  0 is mod(N, P),
  N1 is N // P,
  concat(S, [P], S1),
  decompose(N1, P, S1, R).

prime_divisors(N, Divisors) :-
  decompose(N, 2, [], Divisors).

square_divisors(N, D) :-
  N1 is N * N,
  decompose(N1, 2, [], D).

cube_divisors(N, D) :-
  N3 is N * N * N,
  decompose(N3, 2, [], D).
