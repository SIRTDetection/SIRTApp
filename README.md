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
la galería la app lo primero que hace es solicitarle un token al servidor, este se lo da siempre y cuando se
haya perdido correctamente, esto ha sido implementado como medida de seguridad para que nadie ajeno a la app
pueda hacer uso del servidor, si todo a ido bien, la app manda ese token, que ha almacenado en las sharedpreferences,
 y el servidor comprueba si es correcto, si es asi la app manda ya la imagen, el servidor la procesa(se explicará
como en la parted del server) y devuelve la imagen resultado, todo esto haciendo uso de JSONS y tratando las imágenes
como bitmaps y en algunos casos como ImagenUri, ya con la imagen de respuesta en poder de la app, esta inicia una
nueva actividad en la que muestra la imagen en grande y en la que gracias a una serie de metodos que estan en la clase
asociada a dicha actividad (ImageViewer) podemos hacer zoom sobre ella, si le damos a la tecla de retorno
del movil nos volvera a mostrar la actividad principal debido a que hemos cerrado la actividad de la imagen en grande,
pero ahora, ademas de poder escoger otra imagen y volver a empezar el proceso, podemos clikar en el pequeño
imageViewer que sale arriba de la actividad y se volvera a abrir la imagen en grande, para ello lo que app hace es
inicar de nuevo dicha actividad de la imagen en grande y cargar la ultima imagen modifica que se encuentra en el
almacenamiento del dispositivo. Aprovecho esta última aclaración para destacar que la app guarda las imaganes resultado
que le duvuelve el servidor y que las podemos encontrar en el amacenamiento interno del dispositivo en la carpeta Pictures
en la subcarpeta SirtApp.

A continuaciòn y ya por último añadir los enlaces al javadoc con los comentarios del codigo y un enlace a un
video que muestra el funcionamiento completo de la app:

Javadoc:

Video: