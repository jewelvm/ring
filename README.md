#  RInG, Rewriting for Intermediate Grammar

## What is RInG?

RInG is a Java tree rewriting tool. It was written to aid in the implementation of the JewelVM optimizer and code generator.

## Dependencies

You will need JavaCC to build RInG. You can get it from:

	[https://javacc.org/](https://javacc.org/)

Once you download and extract the JavaCC zip file, make sure its bin folder is in your path.

If missing, create a script file in the bin folder with the following contents:

```
#!/bin/sh
java -classpath "`dirname $0`/lib/javacc.jar" javacc "$@"
```

## Bulding

In order to build RInG, from the root folder, just type:

```
cd src
make
cd ..
```

### Running

In order to run RInG, from the root folder, just type:

```
bin/ring
```

If necessary, add the bin folder to your path.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

