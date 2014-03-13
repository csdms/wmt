

def execute(env):
    for name in ['run_duration', ]:
        env[name] = float(env[name])

    env['sea_level_start_time'] = 0.
    env['sea_level_end_time'] = env['run_duration'] / 365.
