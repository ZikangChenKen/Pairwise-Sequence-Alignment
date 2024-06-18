#!/bin/bash

#SBATCH --job-name=comp322-hw3
#SBATCH --nodes=1
#SBATCH --ntasks-per-node=1
#SBATCH --cpus-per-task=16
#SBATCH --mem=16000m
#SBATCH --time=00:30:00
#SBATCH --mail-user=zc45@rice.edu # TODO: Input your net id
#SBATCH --mail-type=ALL
#SBATCH --export=ALL
#SBATCH --partition=scavenge
#SBATCH --exclusive
 
echo "My job ran on:"
pwd 

echo SLURM_NODELIST=$SLURM_NODELIST
echo USER=$USER

export HW3_FOLDER=/home/$USER/hw3-ZikangChenKen # TODO: Update this path to point to your hw 3 folder

if [[ ! -d $HW3_FOLDER ]]; then
    echo "The designated HW 3 folder ($HW3_FOLDER) does not seem to exist"
    exit 1
fi

cd $HW3_FOLDER

source  /home/mjoyner/comp322/322_setup.sh
java -version
mvn --version

mvn install:install-file -Dfile=./src/main/resources/hjlib-elog-aedancullen-master-d2108184ec-1.jar -DgroupId=com.github.RiceParProgCourse -DartifactId=hjlib-elog-aedancullen -Dversion=master-d2108184ec-1 -Dpackaging=jar
mvn -Dtest=Homework3PerformanceTest clean compile test
