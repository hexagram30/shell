(ns hxgm30.shell.impl.demo
  (:require
    [hxgm30.shell.impl.default :as default])
  (:import
    (java.net InetAddress)
    (java.util Date))
  (:refer-clojure :exclude [empty parse]))

(defrecord DemoShell [
  disconnect-command
  disconnect-handler])

(defn banner
  [this]
  (str "Welcome to "
       (.getHostName (InetAddress/getLocalHost))
       "!\r\n"
       "It is "  (new Date) " now.\r\n"))

(def behaviour
  {:banner banner})
