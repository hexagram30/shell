(ns hxgm30.shell.session.core)

(defrecord LoginData
  [;; `type` should be one of :anonymous or :authenticated
   type
   ;; `attempts` is the number (integer) of login attempts a user has made
   attempts
   ])

(defrecord SessionData
  [id
   ;; type should be a keyword representing one of the supported session
   ;; types, e.g., :telnet, :telnet-ssl, :http.
   type
   ;;
   ;; `login-data` should be an instance of the `LoginData` record
   login-data
   ;; user-data is a hash-map of user-specific data captured or maintained
   ;; in the session.
   user-data
   ;; `shell-stack` is a vector that should be treated as a FIFO
   shell-stack])

