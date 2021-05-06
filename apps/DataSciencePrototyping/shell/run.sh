#!/bin/bash

conda create -n venvgomica3.6.8 python=3.6.8
echo "Environment path = $CONDA_PREFIX"
echo "Conda virtual environment name = $CONDA_DEFAULT_ENV"

conda activate venvgomica3.6.8

pip install -r requirements.txt

export JAVA_HOME=/opt/cloudera/java/jdk1.8.0_60
printenv | grep JAVA_HOME
export PATH=$JAVA_HOME/bin:$PATH


conda deactivate

conda remove --name venvgomica3.6.8 --all

