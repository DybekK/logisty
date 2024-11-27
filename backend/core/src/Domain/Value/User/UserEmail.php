<?php

namespace Core\Domain\Value\User;

use Core\Domain\Value\ValueObject;

/**
 * @implements ValueObject<string, UserEmail>
 */
final readonly class UserEmail implements ValueObject
{
    private function __construct(
        public string $value,
    ) {}

    public static function of(mixed $value): self
    {
        return new self($value);
    }

    public function eq(mixed $object): bool
    {
        return $object instanceof UserEmail && $object->value === $this->value;
    }
}
