#!/bin/bash

# Function to replace the exception import in a file
fix_exception_import() {
    local file=$1

    # Replace old exception import with the correct one
    sed -i 's/import com.restaurant.application.user.exception.UserApplicationException/import com.restaurant.application.user.error.UserApplicationException/g' "$file"

    # Add the error code import if it doesn't exist
    if ! grep -q "import com.restaurant.application.user.error.UserApplicationErrorCode" "$file"; then
        # Find the line with the UserApplicationException import
        local exception_line=$(grep -n "import com.restaurant.application.user.error.UserApplicationException" "$file" | cut -d: -f1)

        if [ -n "$exception_line" ]; then
            # Add the error code import after the exception import
            sed -i "${exception_line}a import com.restaurant.application.user.error.UserApplicationErrorCode" "$file"
        else
            # If no exception import found, add both imports after the package declaration
            local package_line=$(grep -n "^package " "$file" | cut -d: -f1)
            if [ -n "$package_line" ]; then
                sed -i "${package_line}a import com.restaurant.application.user.error.UserApplicationErrorCode\nimport com.restaurant.application.user.error.UserApplicationException" "$file"
            fi
        fi
    fi

    # Replace UserApplicationException.SystemError.code with UserApplicationErrorCode.SYSTEM_ERROR.code
    sed -i 's/UserApplicationException.SystemError.code/UserApplicationErrorCode.SYSTEM_ERROR.code/g' "$file"

    # Replace UserApplicationException.ExternalServiceError.code with UserApplicationErrorCode.EXTERNAL_SERVICE_ERROR.code
    sed -i 's/UserApplicationException.ExternalServiceError.code/UserApplicationErrorCode.EXTERNAL_SERVICE_ERROR.code/g' "$file"
}

echo "Fixing imports in application files..."

# Find and fix handler files in the application module
find domains/user/application/src/main/kotlin -name "*.kt" -exec bash -c 'fix_exception_import "$0"' {} \;

echo "Fixed imports in application files!"
