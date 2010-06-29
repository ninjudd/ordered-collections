ordered-set is a set that maintains the insertion order of its contents.

## Examples

    (ordered-set 4 3 1 8 2)
    => #{4 3 1 8 2}

    (conj (ordered-set 9 10) 1 2 3)
    => #{9 10 1 2 3}

    (into (ordered-set) [7 6 1 5 6])
    => #{7 6 1 5}

    (disj (ordered-set 8 1 7 2 6) 7)
    => #{8 1 2 6}