import os

from wmt.utils.io import (upload_large_file,
                          upload_large_file_to_stage, )


if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser()
    parser.add_argument('file', help='File to upload')
    parser.add_argument('uuid', nargs='?', default=None, help='Run UUID')

    args = parser.parse_args()

    #url = os.path.join('http://csdms.colorado.edu/wmt/run/upload', args.uuid, args.file)
    url = os.path.join('http://csdms.colorado.edu/wmt/run/upload')
    if args.uuid is None:
        resp = upload_large_file(args.file, url)
    else:
        resp = upload_large_file_to_stage(args.file, url, args.uuid)

    print resp.text
