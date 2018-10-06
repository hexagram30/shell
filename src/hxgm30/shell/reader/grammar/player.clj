(ns hxgm30.shell.reader.grammar.player
  (:require
    [hxgm30.registration.components.registrar]
    [hxgm30.shell.reader.grammar.common :as common]
    [hxgm30.shell.util :as util]))

(def command-tree
  "The commands in this tree are for user login."
  (merge
    common/help
    {:commands {
       :help "List the commands available."}
     :exit {
       :help "Leave the registration shell, returning to the top-level shell."}
     :worlds {
       :help (str "List the worlds (game instances) running on the server. "
                  "Worlds with an asterisk (*) by their names are ones where "
                  "you have created one or more characters.")
       :fn #'clojure.core/identity}
     :characters {
       :help "List the characters you have created in a given world."
       :fn #'clojure.core/identity}
     :character {
       :help ""
       :subcommands {
         :info {
           :help "View the details of a given character (one of your own)."
           :fn #'clojure.core/identity}
         :create {
           :help "Create a new playing character on the given world."
           :fn #'clojure.core/identity}}}
     :play {
       :help "Play in the given world with the given character."
       :fn #'clojure.core/identity}}))

(def metaphones
  (util/metaphone2-lookup (keys command-tree)))

(defrecord PlayerGrammar
  [command-tree
   metaphones])

(defn create
  ([]
    (map->PlayerGrammar {:command-tree command-tree
                         :metaphones metaphones})))
