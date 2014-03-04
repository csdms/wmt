# /bin/bash

PREFIX=/usr/local/cca-tools/projects/nced/internal

export PATH=$PREFIX/bin:/bin:/usr/bin
export LD_LIBRARY_PATH=/home/csdms/tools/glib2/2.25/lib
VIRTUAL_ENV_DISABLE_PROMPT=1 source /home/huttone/Canopy/Canopy_64bit/User/bin/activate

RUNUUID=$1
PYTHON=$(which python)
LAUNCHPY=launch.py
LAUNCH_CMD="$PYTHON $LAUNCHPY $RUNUUID"


#export PATH=/usr/local/python-2.7.1/bin:/usr/local/adm/config/python/bin:$PATH

#export PYTHONPATH=/usr/local/python-2.7.1:/usr/local/adm/config/python/lib/python2.7/site-packages
#export LD_LIBRARY_PATH=/usr/local/python-2.7.1/lib:/usr/local/neon-0.29.6/lib/

$LAUNCH_CMD

