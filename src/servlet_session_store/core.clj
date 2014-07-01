(ns servlet-session-store.core
  (:use [ring.middleware.session.store :only (SessionStore)])
  (:import (javax.servlet.http HttpServletRequest HttpSession)))

(defprotocol HttpSessionCoercion
  (^HttpSession as-http-session [_]))

(extend-protocol HttpSessionCoercion
  java.util.Map
  (as-http-session [m] (as-http-session (:servlet-request m)))

  HttpServletRequest
  (as-http-session [request] (.getSession request))

  HttpSession
  (as-http-session [session] session))

(deftype ServletSessionStore [^HttpSession hs]
  SessionStore
  (read-session [_ key] (.getAttribute hs ^String key))
  (write-session [_ key data] (.setAttribute hs ^String key data))
  (delete-session [_ key] (.removeAttribute hs ^String key)))

(defn servlet-session-store [http-session-spec]
  (ServletSessionStore.
   (as-http-session http-session-spec)))

(defn wrap-servlet-session [handler]
  (fn [request]
    (handler
     (assoc request :session (servlet-session-store request)))))
