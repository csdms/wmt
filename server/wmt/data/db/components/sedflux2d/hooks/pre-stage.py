

def execute(env):
    for name in ['run_duration', 'shelf_gradient', 'shelf_width',
                 'slope_gradient', 'domain_width']:
        env[name] = float(env[name])

    env['sea_level_start_time'] = 0.
    env['sea_level_end_time'] = env['run_duration'] / 365.

    env['shelf_slope_break_elevation'] = - (env['shelf_gradient'] *
                                            env['shelf_width'])
    env['end_of_domain_elevation'] = (
        env['shelf_slope_break_elevation'] -
        env['slope_gradient'] * (env['domain_width'] - env['shelf_width'])
    )

