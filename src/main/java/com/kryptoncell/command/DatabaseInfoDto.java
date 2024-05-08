package com.kryptoncell.command;

import java.util.List;

public record DatabaseInfoDto(String host,
                              int port,
                              String database,
                              String user,
                              String password,
                              List<String> tables) {
}
