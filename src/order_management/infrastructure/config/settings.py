"""Configuración de la aplicación."""

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    """Configuración cargada desde variables de entorno."""

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        case_sensitive=False,
    )

    database_url: str = "postgresql+asyncpg://postgres:postgres@localhost:5432/order_management"
    api_prefix: str = "/api/v1"


_settings: Settings | None = None


def get_settings() -> Settings:
    """Retorna la configuración (singleton)."""
    global _settings
    if _settings is None:
        _settings = Settings()
    return _settings
