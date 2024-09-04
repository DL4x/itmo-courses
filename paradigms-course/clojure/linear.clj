(defn st-abstract [foo]
  (fn [st1 st2]
    (cond
      (number? st1) (foo st1 st2)
      (vector? st1) (mapv (st-abstract foo) st1 st2))))

(defn st1*st2 [foo]
  (fn [st1 st2]
    (let [f #(foo % st2)] (mapv f st1))))

;; v+/v-/v*/vd – покоординатное сложение/вычитание/умножение/деление
(def v+ (st-abstract +))
(def v- (st-abstract -))
(def v* (st-abstract *))
(def vd (st-abstract /))

;; scalar – скалярное
(defn scalar [v1 v2]
  (apply + (v* v1 v2)))

;; vect - векторное произведение
(defn vect [v1 v2]
  (vector
    (- (* (nth v1 1) (nth v2 2)) (* (nth v1 2) (nth v2 1)))
    (- (- (* (nth v1 0) (nth v2 2)) (* (nth v1 2) (nth v2 0))))
    (- (* (nth v1 0) (nth v2 1)) (* (nth v1 1) (nth v2 0)))))

;; v*s – умножение на скаляр
(def v*s (st1*st2 *))

;; m+/m-/m*/md – поэлементное сложение/вычитание/умножение/деление
(def m+ (st-abstract v+))
(def m- (st-abstract v-))
(def m* (st-abstract v*))
(def md (st-abstract vd))

;; m*s – умножение на скаляр
(def m*s (st1*st2 v*s))

;; m*v – умножение на вектор
(def m*v (st1*st2 scalar))

;; transpose – транспонирование
(defn transpose [m]
  (apply mapv vector m))

;; m*m – матричное умножение
(defn m*m [m1 m2]
  (mapv (partial m*v (transpose m2)) m1))

;; s+/s-/s*/sd – поэлементное сложение/вычитание/умножение/деление
(def s+ (st-abstract +))
(def s- (st-abstract -))
(def s* (st-abstract *))
(def sd (st-abstract /))
