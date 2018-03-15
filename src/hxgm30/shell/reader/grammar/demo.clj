(in-ns 'hxgm30.shell.reader.grammar)

(def demo-command-tree
  {"" true
   "logout" true
   "say" {:depth 1
          :any true}})
