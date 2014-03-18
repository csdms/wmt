import os
import requests


URL = 'https://csdms.colorado.edu/wmt-server/run/get'


def get_status(uuid):
    resp = requests.get(os.path.join(URL, str(uuid)))
    if resp.status_code == 200:
        return resp.text
    else:
        raise ValueError(uuid)


if __name__ == '__main__':
    import argparse

    parser = argparse.ArgumentParser()
    parser.add_argument('id', nargs='*', help='Get status of simulations')

    args = parser.parse_args()

    for id in args.id:
        print get_status(id)
