"""Configuración de la aplicación."""

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    """Configuración cargada desde variables de entorno."""

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        case_sensitive=False,
    )

    database_url: str = "postgresql+asyncpg://localhost:5432/order_management"
    api_prefix: str = "/api/v1"


_settings_cache: Settings | None = None


def get_settings() -> Settings:
    """Return application settings (singleton)."""
    global _settings_cache  # noqa: PLW0603
    if _settings_cache is None:
        _settings_cache = Settings()
    return _settings_cache
