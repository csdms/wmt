
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
