# sentence-splitter

## Usage

```clojure
(:require [sentence-splitter.core :refer [build-splitter-params split-sentence]])

;; splitter-params is a map with params needed by sentence splitting
(def splitter-params (build-splitter-params))

(split-sentence "A HEADLINE\nAn example sentence. Anther example end with question mark?" splitter-params)
;; => ["A HEADLINE" "An example sentence." "Anther example end with question mark?"]
```

## License

Copyright © 2021

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
