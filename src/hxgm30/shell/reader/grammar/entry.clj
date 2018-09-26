(ns hxgm30.shell.reader.grammar.entry
  (:require
    [hxgm30.registration.components.registrar]
    [hxgm30.shell.formatter :as formatter]
    [hxgm30.shell.util :as util]))

(def command-tree
  "The commands in this tree are specifically only those supported for
  anonymous users, when they first connect to the service."
  {:commands {
     :help "List the commands available."}
   :quit {
     :help "Disconnect from the MUSH server."}
   :help {
     :help (str "Get the documentation for supported commands and any of "
                "their subcommands. Usage is of the following form: "
                formatter/new-line
                "help <COMMAND> [<SUBCOMMAND> [<SUBCOMMAND> ...]]")}
   :login {
     :help (str "Log in to a game instance on the server. Takes two "
                "arguments: the user name and the game instance to "
                "join. The user will then be prompted to enter their "
                "password."
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
         :fn #'hxgm30.registration.components.registrar/reset-player-key}}}})

(def metaphones
  (util/metaphone2-lookup (keys command-tree)))

(defrecord EntryGrammar
  [command-tree
   metaphones])

(defn create
  ([]
    (map->EntryGrammar {:command-tree command-tree
                        :metaphones metaphones})))
