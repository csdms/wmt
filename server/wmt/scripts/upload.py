import os

from wmt.utils.io import upload_large_file


if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser()
    parser.add_argument('file', help='File to upload')
    parser.add_argument('uuid', help='Run UUID')

    args = parser.parse_args()

    #url = os.path.join('http://csdms.colorado.edu/wmt/run/upload', args.uuid, args.file)
    url = os.path.join('http://csdms.colorado.edu/wmt/run/upload')
    resp = upload_large_file(args.file, url)

    print resp.text
