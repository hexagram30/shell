(in-ns 'hxgm30.shell.reader.grammar)

(def entry-command-tree
  "The commands in this tree are specifically only those supported for
  anonymous users, when they first connect to the service."
  {:entry {
     :help "Top-level commands available upon connection to the server."
     :commands {
       :help {
         :help (str "Get the documentation for supported commands and any of "
                    "their subcommands. Usage is of the following form: "
                    "help <COMMAND> [<SUBCOMMAND> [<SUBCOMMAND> ...]]")}
       :login {
         :help (str "Log in to a game instance on the server. Takes two "
                    "arguments: the user name and the game instance to "
                    "join. The user will then be prompted to enter their "
                    "password.\n\nSupported game instances: ")
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
         :help (str "Perform one or more type of account resets.\n\nSupported "
                    "subcommands: password, player-key")
         :subcommands {
           :password  {
             :help "Reset the password for a given account."
             :fn #'hxgm30.registration.components.registrar/reset-password}
           :player-key {
             :help "Reset the player key for a given account."
             :fn #'hxgm30.registration.components.registrar/reset-player-key}}}}}})
