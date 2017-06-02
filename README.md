# PdfExtract

Swing application to use with `CalibreHelper`.

## Feature

This application extracts all images on a PDF file.

## Install

Create a install with `batch file`:

```shell
@echo off

set "JAVA_EXEC=javaw"
set "JAVA_OPTS= -Xms1024m -Xmx1024m"

START "PdfExtract" /B %JAVA_EXEC% %JAVA_OPTS% -cp "lib\*;" br.com.yahoo.mau_mss.pdfextract.PdfExtract >> logs\stdout.log 2>&1
```

##Usage

Choose de source folder

- Search for PDF files and extract its images

## Credits
[Mauricio Soares da Silva](mailto:maumss.git@gmail.com).

## License

[GNU General Public License (GPL) v3](http://www.gnu.org/licenses/)

Copyright &copy; 2012 Mauricio Soares da Silva

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.

