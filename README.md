# SIRTApp


SirtApp es la aplicación que hemos desarrollado para poder ponerse en contacto con el servidor
de una forma sencilla y cómoda para el usuario, con ella el usuario puede enviar fotos directamente
al servidor y ver la imagen que este devuelve con los objetos identificados, pudiendo el usuario
incluso hacer zoom sobre ellos.

La app ha sido desarrollada en la API 28(Android 9) aunque se ha hecho conpatible con todas las versiones
a partir de la API 19(Android 4), en el Android Studio haciendo uso de Java, por seguridad añadimos
compatibilidad con C++ (aunque al final no fue utilizado), tambien cabe destacar que a la hora de comunicarse
la app con el servidor, debido a que este ultimo estaba en phyton, hemos utilizado JSON para mayor
fiabilidad a la hora de comunicar ambos sistemas.

Cuando la app se inicia nos muestra una pequeña introducción que explica brevemente en que consiste la
app y que permisos necesita para funcionar, depues de condecer dichos permisos pasamos a ver un activity_main
conun aviso(que comentare mas adelante) y dos botones, hago un pequeño parentesis para puntualizar que todos
los textos que componen la interfaz se encuentran disponibles en español e ingles (dependiendo del idioma
en el que este el dispositivo android donde se este ejecutando), aqui podemos pulsar el botón de la cámara o
el de la galería, que nos permitiran o abrir la camara y sacar una foto o elegir una desde la galeria del
teléfono respectivamente y se manda directamente al servidor, este procesa la imagen y devuelve la misma
imagen pero con los objetos ya identificados, como este proceso dura entre 5 y 10 segundos dependiendo de
la complejidad de la imagen el trafico de internet y la propia conexion a la red del usuario, mientras eso
ocurre la app nos mostrara una barra de progeso en el que nos informa de que esta haciendo en concreto,
despues de esto la app nos muestra automaticamente la imagen con los objetos identificados por el servidor
en grande y en la cual podemos hacer zoom con los dedos para que se vea bien los textos que devuelve el servidor,
en este punto podemos cerrar la app o, si pulsamos la flecha de retorno en el telefono, nos volvera a salir la
activity inicial pero con la imagen en pequeño arriba, ahora podemos volver a coger una foto y repetir el
proceso o, como dice el mensaje explicativo, como ya está la imagen arriba de la pantalla podemos pulsar
sobre ella para volver a ponerla en grande.

Pasamos ahora a explicar el funcionamiento interno de la app, cuando el usuario saca una foto o la coge de
la galeria la app...