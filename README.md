# servlet-session-store

An implementation of the ring SessionStore protocol by relying on the
session implementation from the underlying servlet request. This
assumes that a servlet request is accessible in the ring request map
under the :servlet-request key. The ring-jetty-servlet-adapter is an
example of how to inject a servlet request into the request map using
jetty.

## Usage

(use '[servlet-session-store.core :only (wrap-servlet-session)])

(wrap-servlet-session ring-handler)

(use '[ring.middleware.session.store :only (read-session)])

(defn view [request]
  (let [session-store (:session request)]
    (read-session session-store "foo")))

## License

Copyright (C) 2012 Robert Marianski

Distributed under the Eclipse Public License, the same as Clojure.
