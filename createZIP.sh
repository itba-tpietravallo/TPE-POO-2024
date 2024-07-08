#!/bin/bash

ZIP_FILE_NAME="TPE-POO-2024-GRUPO-05"
tmp_dir=".verify"

# If tmp_dir is empty
if [ -z "$tmp_dir" ]; then
    echo "tmp_dir is not defined. Warning: rm -rf of empty variable"
    exit 1;
fi

rm -rf "$tmp_dir" &> /dev/null
rm "$ZIP_FILE_NAME" &> /dev/null
rm "$ZIP_FILE_NAME.zip" &> /dev/null

# Create a zip file containing all necessary files
zip -r "$ZIP_FILE_NAME.zip" . -x *.idea* -x *out\* -x *.iml* -x *.DS_Store* -x *createZIP.sh* -x "$ZIP_FILE_NAME.zip" -x *$ZIP_FILE_NAME* | egrep 'warning' &> /dev/null
all_files_found="$?"
if [ "$all_files_found" -eq "0" ]; then
    echo "Some files were not found. Exiting..."
    rm "$ZIP_FILE_NAME.zip" &> /dev/null
    exit 1
fi

# Verify the zip file
unzip -q "$ZIP_FILE_NAME.zip" -d "$tmp_dir"

# For each file in the source, make sure its part of the decompressed output
for file in `find ./$tmp_dir`; do
    if [ ! -e "$file" ]; then
        echo "File $file not found in output folder"
        exit 1
    elif [ $# -ne 0 ]; then
        echo "File $file found in output folder"
    fi
done

rm -rf $tmp_dir &> /dev/null