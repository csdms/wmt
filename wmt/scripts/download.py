from wmt.utils.io import download_file
import os

def download_run_tarball(uuid):
    import requests

    url = os.path.join('http://csdms.colorado.edu/wmt/run/download')
    resp = requests.post(url, stream=True,
                         data={
                             'uuid': uuid,
                             'filename': '',
                         })

    dest_name = uuid + '.tar.gz'
    with open(dest_name, 'wb') as fp:
        for chunk in resp.iter_content():
            if chunk: # filter out keep-alive new chunks
                fp.write(chunk)
                fp.flush()

    return dest_name


if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser()
    parser.add_argument('id', help='run id to download')

    args = parser.parse_args()
    print download_run_tarball(args.id)

