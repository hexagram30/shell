(in-ns 'hxgm30.shell.reader.grammar)

(def login-command-tree
  {"" true
   "QUIT" true
   "repl" true
   "login" {:depth 1
            :any true}
   "create" {:depth 1
             :any true}})
