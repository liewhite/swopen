package io.github.liewhite.config

case class ConfigError(key: String) extends Exception(key)