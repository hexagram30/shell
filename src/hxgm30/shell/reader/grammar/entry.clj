(ns hxgm30.shell.reader.grammar.entry
  (:require
    [hxgm30.registration.components.registrar]
    [hxgm30.shell.formatter :as formatter]
    [hxgm30.shell.reader.grammar.common :as common]
    [hxgm30.shell.util :as util]))

(def command-tree
  "The commands in this tree are specifically only those supported for
  anonymous users, when they first connect to the service."
  (merge
    common/help
    {:commands {
       :help "List the commands available."}
     :quit {
       :help "Disconnect from the MUSH server."}
     :login {
       :help (str "Log into a game instance on the server. Providing a user "
                  "name only will put you into the player shell. Providing a "
                  "user name as well as a game instance will result in a list "
                  "of character names that player has in the given game, one of "
                  "which will need to be selected. Providing a user name, game "
                  "instance, and a player (character) name will put you "
                  "directly into the game world. In all cases, you will be "
                  "prompted for a password."
                  formatter/new-line
                  "Supported game instances: ")
       ;; The following needs to be provided by the master project/game
       ;; server. When called, it will provide a list of supported game
       ;; instances.
       :help-fn (constantly :not-implemented)
       ;; The following needs to be provided by the master project/game
       ;; server. When called, it will kick off the login workflow.
       :fn (constantly :not-implemented)}
     :register {
       :help (str "Create a user account. Takes one argument, the user name. "
                  "The user will then be prompted to enter a password.")
       :fn #'hxgm30.registration.components.registrar/create-user}
     :reset {
       :help (str "Perform one or more type of account resets.")
       :subcommands {
         :password  {
           :help "Reset the password for a given account."
           :fn #'hxgm30.registration.components.registrar/reset-password}
         :player-key {
           :help "Reset the player key for a given account."
           :fn #'hxgm30.registration.components.registrar/reset-player-key}}}}))

(def metaphones
  (util/metaphone2-lookup (keys command-tree)))

(defrecord EntryGrammar
  [command-tree
   metaphones])

(defn create
  ([]
    (map->EntryGrammar {:command-tree command-tree
                        :metaphones metaphones})))
