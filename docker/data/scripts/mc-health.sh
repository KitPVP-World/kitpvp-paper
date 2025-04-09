#!/bin/bash

mc-monitor status "${MC_HEALTH_EXTRA_ARGS[@]}" --host "${SERVER_HOST:-localhost}" --port "${SERVER_PORT:-25565}"
exit $?