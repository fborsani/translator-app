# Readme
A mobile translation app that I wrote to learn mobile programming. This app uses the Deepl API service to perform translation and allows translating text from the clipboard, a text file, from an image in the gallery or from a picture taken on the spot.<br/>
The image parsing is done by using the Opencv image editing library to optimize the image for OCR and the Java implementation of Tesseract to extract the text.<br/>
To improve the character detection accuracy it is possible to download language-specific training data used by the OCR library. The files can be downloaded or removed in the dedicated config tab.<br/>
Since the translation is performed by invoking the Deepl APIs, the user must provide a valid API Key in the Config tab. The key is then stored in the application's Sqlite database so it can be specified only once.<br/>
The app will allow to verify the validity of the key and keep track of the translated characters quota.
