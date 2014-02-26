import requests


def _copy_a_chunk(src_fp, dest_fp, chunk_size, checksum=None):
    chunk = src_fp.read(chunk_size)
    if not chunk:
        raise EOFError
    dest_fp.write(chunk)
    if checksum is not None:
        checksum.update(chunk)


def chunk_copy(src_fp, dest_fp, chunk_size=8192, checksum=None):
    while 1:
        try:
            _copy_a_chunk(src_fp, dest_fp, chunk_size, checksum=checksum)
        except EOFError:
            break


def upload_large_file(filename, url):
    with open(filename, 'r') as fp:
        return requests.post(url, data=fp)


def download_file(url):
    resp = requests.get(url, stream=True)

    try:
        for attr in resp.headers['content-disposition'].split(';'):
            if attr.strip().startswith('filename'):
                dest_name = attr[attr.index('=') + 1:].strip()
    except KeyError:
        dest_name = os.path.basename(url)

    with open(dest_name, 'wb') as fp:
        for chunk in resp.iter_content():
            if chunk: # filter out keep-alive new chunks
                fp.write(chunk)
                fp.flush()

    return dest_name
